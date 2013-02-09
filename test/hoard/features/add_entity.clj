(ns hoard.features.add-entity
  (:use hoard.features.common midje.sweet))

(def entity {:name "test"})
(def uri (str "http://localhost:3000/tests"))

(with-server
  (fact "posting an entity to the collection should create a resource and tell you where its located"
    (client-deletes-resource uri)
    (let [response (client-creates-resource uri entity)]
      (:status response) => 201
      (get (:headers response) "location") => #"http://localhost:3000/tests/[A-Za-z0-9]{24}"))
  
  (fact "posting an enitty to the collection should make it appear in index resource"
    (client-deletes-resource uri)
    (let [resource-uri (get (:headers (client-creates-resource uri entity)) "location")
          index-body (:body (client-lists-resource-index uri))]
      (print index-body)
      (:href (first (:links (first (:tests index-body))))) => resource-uri))

)
