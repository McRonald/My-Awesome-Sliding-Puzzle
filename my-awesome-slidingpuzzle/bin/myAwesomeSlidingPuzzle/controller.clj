(ns myAwesomeSlidingPuzzle.controller
  (:use compojure.core)
  (:require [compojure.core :as compojure]
            [myAwesomeSlidingPuzzle.view :as view]
            [myAwesomeSlidingPuzzle.model :as model]))

(defn start-page []
  (model/reset-game!)
  (view/play-screen))

(defn turn-page [button-pressed]
  (let [button-id (name (first (keys button-pressed)))
        rownr (Integer/parseInt (str (second button-id)))
        colnr (Integer/parseInt (str (nth button-id 2)))]
    (model/play! rownr colnr)
      (if (model/complete?)
        (view/complete-screen)
        (view/play-screen))))

(defroutes sliding-puzzle-routes
  (GET "/" [] (start-page))
  (POST "/" {button-pressed :params} (turn-page button-pressed)))