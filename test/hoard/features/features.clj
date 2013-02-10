(ns hoard.features.features
  (:use hoard.features.common midje.sweet))

(def entity {:name "test"})
(def updated-entity {:name "updated-test"})
(def uri (str "http://localhost:3000/tests"))

(with-server
  (fact "heading the resource index should tell the client about the resource" 
    (let [headers (:headers (client-heads-resource-index uri))]
      (get headers "accept") => "application/json"
      (get headers "allow") => "GET, POST, DELETE"))
  
  (fact "posting an entity to the resource index should create a subordinate resource and tell the client where its located"
    (client-deletes-resource uri)
    (let [response (client-creates-resource uri entity)]
      (:status response) => 201
      (get (:headers response) "location") => #"http://localhost:3000/tests/[A-Za-z0-9]{24}"))


  (fact "deleting the resource index should remove all subordinate resources"
    (client-deletes-resource uri)
    (client-creates-resource uri entity)
    (client-deletes-resource uri)
      (let [index-body (:body (client-lists-resource-index uri))]
        (count (:tests index-body)) => 0))

  (fact "creating subordinate resources of the resource index should be refered within the resource index"
    (client-deletes-resource uri)
    (let [resource-uri (get (:headers (client-creates-resource uri entity)) "location")
          index-body (:body (client-lists-resource-index uri))]
      (:href (first (:links (first (:tests index-body))))) => resource-uri))
  
  (fact "getting a subordinate resource should return its entity representation"
    (client-deletes-resource uri)
    (let [subordinate-uri (get (:headers (client-creates-resource uri entity)) "location")
          subordinate-body (:body (client-gets-subordinate-resource subordinate-uri))]
      subordinate-body => (contains entity)))

  (fact "querying the resource index with an id should tell the client where the subordinate resource is located" 
    (client-deletes-resource uri)
    (let [subordinate-uri (get (:headers (client-creates-resource uri entity)) "location")
          subordinate-body (:body (client-gets-subordinate-resource subordinate-uri))
          response (client-queries-resource-index-with-id uri (:_id subordinate-body))]
      (:status response) => 303
      (get (:headers response) "location") => #"http://localhost:3000/tests/[A-Za-z0-9]{24}"))

  (fact "deleting an existing subordinate resource should remove it"
    (client-deletes-resource uri)
      (let [subordinate-uri (get (:headers (client-creates-resource uri entity)) "location")]
        (client-deletes-resource subordinate-uri)
        (:status (client-gets-subordinate-resource subordinate-uri)) => 404))

  (fact "heading a subordinate resource should tell the client about the resource"
    (client-deletes-resource uri)
    (let [subordinate-uri (get (:headers (client-creates-resource uri entity)) "location")
          headers (:headers (client-heads-resource-index subordinate-uri))]
      (get headers "accept") => "application/json"
      (get headers "allow") => "GET, PUT, DELETE"))

  (fact "putting to an existing subordinate resource should update the entity"
    (client-deletes-resource uri)
    (let [subordinate-uri (get (:headers (client-creates-resource uri entity)) "location")
          put-resposne (client-puts-to-resource subordinate-uri updated-entity)
          subordinate-body (:body (client-gets-subordinate-resource subordinate-uri))]
      (:status put-resposne) => 204
      subordinate-body => (contains updated-entity)))

  `(fact "putting to a subordinate resource that does not exist should tell the client the subordinate resrouce does not exist")

  (fact "putting to the resource index should tell the client the method is now allowed"
    (let [response (client-puts-to-resource uri entity)]
      (:status response) => 405))

  (fact "posting to a subordinate resource should tell the client the method is now allowed"
    (let [response (client-creates-resource (str uri "/f8q9nqhncufihqqmfc8hq3c") entity)]
      (:status response) => 405)))
