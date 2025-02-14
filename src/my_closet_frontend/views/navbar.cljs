(ns my-closet-frontend.views.navbar
  (:require [re-frame.core :as re-frame]
            [reagent.core :as r]))

(defonce menu-open? (r/atom false))

(defn toggle-menu []
      (swap! menu-open? not))

(defn close-menu []
      (reset! menu-open? false))

(defn navbar []
      [:nav.navbar
       [:a.logo {:href "/"} "MY CLOSET ©"]
       [:div.menu {:class (when @menu-open? "active")}
        [:a {:href "/get-recommendation" :on-click close-menu} "Get recommendation"]
        [:a {:href "#" :on-click close-menu} "Liked"]
        [:a {:href "#" :on-click close-menu} "Favorites"]
        [:a {:href "#" :on-click close-menu} "My clothes"]
        ]
       [:div.hamburger {:on-click toggle-menu} "☰"]])
