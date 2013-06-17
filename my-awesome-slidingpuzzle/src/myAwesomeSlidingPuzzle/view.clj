(ns myAwesomeSlidingPuzzle.view
  (:use hiccup.form
        [hiccup.def :only [defhtml]]
        [hiccup.element :only [link-to]]
        [hiccup.page :only [html5 include-css]])
  (:require [myAwesomeSlidingPuzzle.model :as model]))

(defhtml layout [& content]
  (html5
   [:head
    [:title "Welcome to my awesome sliding puzzle!"]
    (include-css "/css/stylesheet.css")]
   [:body [:div#wrapper content]]))

(defn cell-html [rownum colnum src with-submit?] 
  [:td 
   [:input {:name (str "b" rownum colnum) 
            :src (str src)
            :type (str "image")
            :alt (if with-submit? 
                    "submit" 
                    "")}]])
  
(defn row-html [rownum row with-submit?]
  [:tr (map-indexed (fn [colnum cell]
                      (cell-html rownum colnum cell with-submit?))
                    row)])
  
(defn board-html [board with-submit?]
  (form-to [:post "/"]
           [:table {:cellspacing 0}
            (map-indexed (fn [rownum row]
                           (row-html rownum row with-submit?)) 
                         board)]))

(defn play-screen []
  (layout
    [:div 
     [:p "Come on! slide those pieces.."]
     (board-html (model/get-board) true)]))

(defn complete-screen []
  (layout
    [:div 
   [:p "Congratulations you did it!"]
   (board-html (model/get-board) false)
   (link-to "/" "Shuffle")]))