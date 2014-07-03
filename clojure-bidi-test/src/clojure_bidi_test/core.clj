(ns clojure-bidi-test.core
  "This is an extremely simple RESTful labyrinth API.
   1. Run lein ring server and go to localhost:3000 to find out how to move around the labirynth
   2. Inspecting the code shows that there are 3 places where the string \"/vertex\" is used.
      That's no good as it's error prone. In particular we want to use bidi library to reduce this to number to 1
   3. Go through the bidi tutorial at https://github.com/juxt/bidi#take-5-minutes-to-learn-bidi-using-the-repl
   4. Refactor the code below so that:
      a) bidi is used ;)
      b) there is only one place where string /vertex is used
      c) all other functionality is not changed
   5. Add a possibility to modify the labirynth
      a) you should be able to PUT /vertex/:id with body
      b) you should be able to PUT /edge/:from/:to
      c) make sure the /vertex is still not duplicated

      Hint: you'll probably need to use ANY instead of GET to achieve that. That's fine.
      "
  (:require [compojure.core :refer [defroutes GET POST]]
            [ring.middleware.json :as middleware]
            [ring.util.response :as resp]
            [clojure.walk :as walk]
            [bidi.bidi :refer (make-handler)]
            [bidi.bidi :refer :all]))


(def labyrinth (atom {:root       :A
                      :neigh-list {:A {:neigh {:north :B :south :C} }
                                   :B {:neigh {:east :D :south :A}}
                                   :C {:neigh {:west :E}}
                                   :D {:neigh {:south :A}}
                                   :E {:neigh {:north :A} :treasure true }}}))

(defn neighbours-of [id] (get-in @labyrinth [:neigh-list id]))

(declare bidi-wrap)
(declare bidi-routes)

(defn vertex [{:keys [params]}]
  (let [id (-> params :id keyword)]
    {:body (bidi-wrap (neighbours-of id))}))
(defn root [r] (resp/redirect (path-for bidi-routes vertex :id (:root @labyrinth))))


(def bidi-routes ["/" {""        root
                       "vertex/" {[:id] vertex}}])

(defn bidi-wrap [body]
  {:data (dissoc body :neigh)
   :links (map (fn [[from to]]
                 (let [href (path-for bidi-routes vertex :id to)]
                   (println "href" vertex href bidi-routes to)
                   {:rel from :href href})) (:neigh body))})

(def routes (make-handler bidi-routes))

(def handler
  (-> routes
      (middleware/wrap-json-body {:keywords? true})
      middleware/wrap-json-response))


