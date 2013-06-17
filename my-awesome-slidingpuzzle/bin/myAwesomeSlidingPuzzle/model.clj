(ns myAwesomeSlidingPuzzle.model
  (:require [noir.session :as session]))
(def main-shuffle-image "/images/Baby-Soldier-Funny_09.png")

(def list-images (vector "/images/Baby-Soldier-Funny_01.png"
                       "/images/Baby-Soldier-Funny_02.png"
                       "/images/Baby-Soldier-Funny_03.png"
                       "/images/Baby-Soldier-Funny_04.png"
                       "/images/Baby-Soldier-Funny_05.png"
                       "/images/Baby-Soldier-Funny_06.png"
                       "/images/Baby-Soldier-Funny_07.png"
                       "/images/Baby-Soldier-Funny_08.png"
                       (str main-shuffle-image)))

(def possible-combo [[4 6 5 1 0 7 2 3 8]
                     [6 1 2 4 5 7 0 3 8]
                     [1 6 5 4 2 0 3 7 8]
                     [7 2 3 1 5 0 4 6 8]])

;(def shuffle-list-images (merge (shuffle (butlast list-images)) (nth list-images 8)))

(defn empty-board [images combo] [[(images (combo 0)) (images (combo 1)) (images (combo 2))]
                  [(images (combo 3)) (images (combo 4)) (images (combo 5))]
                  [(images (combo 6)) (images (combo 7)) (images (combo 8))]])

(def init-state {:board (empty-board list-images (possible-combo  (rand-int 4)))})

(defn reset-game! []
  (session/put! :game-state init-state))

(defn get-board []
  (:board (session/get :game-state)))

(defn get-board-cell
  ([row col]
    get-board-cell (get-board) row col)
  ([board row col]
    str (get-in board [row col])))

(defn get-position-main-shuffle [board]
  (def start-val-x 0)
  (def start-val-y 0)
  (loop [x start-val-x y start-val-y]
  (if (= (str main-shuffle-image) (get-board-cell board x y))
    (vector x y)
    (recur (if (= y 2) (+ x 1) x)
           (if (= y 2) 0 (+ y 1))))))

(defn complete?
  ([] (complete? (get-board)))
  ([board]
    (boolean (= (flatten (get-board)) list-images))))

(defn move-piece [board row1 col1 pos]
  (let [row2 (get pos 0)
        col2 (get pos 1)]
    (let [v1 (get-board-cell board row1 col1)
        v2 (get-board-cell board row2 col2)]
       (-> board 
         (assoc-in [row1 col1] v2)
         (assoc-in [row2 col2] v1)))))

(defn is-allowed-to-move? [row1 col1]
(let [pos (get-position-main-shuffle (get-board))]  
  (let [row (get pos 0)
          col (get pos 1)]
      (let [allowed-coords [[row (- col 1)] [row (+ col 1)] [(- row 1) col ] [(+ row 1) col ]]]
         (boolean (some (fn [coords]
                (= coords [row1 col1]))
                   allowed-coords))))))

(defn new-state [row col old-state] ()
  (if (or (and (nil? row )(nil? col)) (not (is-allowed-to-move? row col)))
    old-state
    {:board (move-piece (:board old-state) row col (get-position-main-shuffle (:board old-state)))}))

(defn play! [row col]
  (session/swap! (fn [session-map]
                   (assoc session-map :game-state 
                          (new-state row col (:game-state session-map))))))