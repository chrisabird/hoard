(ns hoard.core
  (:use compojure.core cheshire.core cheshire.generate)
  (:require [compojure.handler :as handler] [somnium.congomongo :as m]))


;; setup json encoder
(add-encoder org.bson.types.ObjectId 
  (fn [value output]
    (.writeString output (.toString value))))

;; setup database connection
(def conn (m/make-connection "hoard" :host "127.0.0.1" :port 27017))
(m/set-connection! conn)

;; data store interaction
(defn insert-entity! [col entity]
  (:_id (m/insert! col entity)))

(defn get-entity [col id]
  (m/fetch-by-id col (m/object-id id)))

(defn delete-all-entities! [col]
  (m/drop-coll! col))

(defn get-all-entities [col] 
  (m/fetch col))

(defn delete-entity! [col id]
  (m/destroy! col {:_id (m/object-id id)}))

(defn update-entity! [col id entity] 
  (m/update! col {:_id (m/object-id id)} entity))

;; resource index handlers
(defn parse-request-body [entity]
  (parse-stream (java.io.BufferedReader. (java.io.InputStreamReader. entity "UTF-8"))))

(defn add-resource-index-headers [response]
  (merge {"accept" "application/json" "allow" "GET, POST, DELETE"} response))

(defn add-subordinate-resource-headers [response]
  (merge {"accept" "application/json" "allow" "GET, PUT, DELETE"} response))

(defn generate-resource-index-entry [resource collection-name]
  {:links [{:href (str "http://localhost:3000/" collection-name "/" (:_id resource))}]}) 

(defn generate-resource-index-body [collection-name resources]
  (let [entries (map #(generate-resource-index-entry % collection-name) resources)]
    (generate-string {(symbol collection-name) entries})))

(defn get-resource-index [collection-name query]
  (if-let [id (get query "id")]
    {:headers (add-resource-index-headers {"location" (str "http://localhost:3000/" collection-name "/" id)}) 
     :status 303}
    {:headers (add-resource-index-headers {}) 
     :body (generate-resource-index-body collection-name (get-all-entities collection-name))}))

(defn create-resource [collection-name entity]
  (let [id (insert-entity! collection-name (parse-request-body entity))
        subordinate-uri (str "http://localhost:3000/" collection-name "/" id)]
    {:headers (add-resource-index-headers {"location" subordinate-uri}) 
     :status 201}))

(defn delete-resource-index [collection-name]
  (delete-all-entities! collection-name)  
  {:headers (add-resource-index-headers {}) 
   :status 200})

(defn head-resource-index [] 
  {:headers (add-resource-index-headers {}) :status 200})

(defn put-resource-index [] 
  {:headers (add-resource-index-headers {}) :status 405})

;; subordinate resource handlers
(defn get-subordinate-resource [collection-name id]
  (if-let [entity (get-entity collection-name id)]
    {:headers (add-subordinate-resource-headers {}) :status 200 :body (generate-string entity)}
    {:headers (add-subordinate-resource-headers {}) :status 404}))

(defn delete-subordinate-resource [collection-name id]
  (delete-entity! collection-name id)
  {:headers (add-subordinate-resource-headers {}) :status 200})

(defn head-subordinate-resource [] 
  {:headers (add-subordinate-resource-headers {}) :status 200})

(defn post-subordinate-resource [] 
  {:headers (add-subordinate-resource-headers {}) :status 405})

(defn put-subordinate-resource [collection-name id entity]
  (update-entity! collection-name id (parse-request-body entity))
  {:headers (add-subordinate-resource-headers {}) :status 204})

;;Routing
(defroutes app-routes 
  (HEAD "/:collection" [] (head-resource-index))
  (GET "/:collection" {{collection :collection} :params query :query-params} (get-resource-index collection query))
  (PUT "/:collection" [] (put-resource-index))
  (POST "/:collection" {{collection :collection} :params body :body} (create-resource collection body))
  (DELETE "/:collection" [collection] (delete-resource-index collection))
  (HEAD "/:collection/:id" [] (head-subordinate-resource))
  (GET "/:collection/:id" [collection id] (get-subordinate-resource collection id))
  (PUT "/:collection/:id" {{collection :collection id :id} :params body :body} (put-subordinate-resource collection id body))
  (POST "/:collection/:id" [] (post-subordinate-resource))
  (DELETE "/:collection/:id" [collection id] (delete-subordinate-resource collection id)))
(def app (handler/api app-routes))
