(ns my-closet-frontend.views.footer
  (:require [re-frame.core :as re-frame]))

(defn footer []
      [:footer.footer
       [:a.logo {:href "#home"} "MY CLOSET ©"]
       [:p.small-text "Made with 🩷 by JK"]
       [:p.small-text "2025"]])