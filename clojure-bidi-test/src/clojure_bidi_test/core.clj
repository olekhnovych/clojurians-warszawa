(ns clojure-bidi-test.core
  (:require [compojure.core :refer [defroutes GET POST]]
            [ring.middleware.json :as middleware]
            [ring.util.response :as resp]
            [clojure.walk :as walk]
            [bidi.bidi :refer (make-handler)]
            [bidi.bidi :refer :all]))


(def labyrinth (atom {:root       :A
                      :neigh-list {:A {:neigh {:north :B :south :C}}
                                   :B {:neigh {:east :D :south :A}}
                                   :C {:neigh {:west :E}}
                                   :D {:neigh {:south :A}}
                                   :E {:neigh {:north :A} :treasure true}}}))

(defn wrap [body]
  {:data (dissoc body :neigh)
   :links (map (fn [[from to]] {:rel from :href (str "/vertex/" (name to))}) (:neigh body))})

(defn neighbours-of [id] (get-in @labyrinth [:neigh-list id]))

(defroutes routes
  (GET "/" req (resp/redirect (str "/vertex/" (-> @labyrinth :root name))))
  (GET "/vertex/:id" {:keys [params]}
       (let [id (-> params :id keyword)]
         {:body (wrap (neighbours-of id))})))

(def handler
  (-> routes
      (middleware/wrap-json-body {:keywords? true})
      middleware/wrap-json-response))


