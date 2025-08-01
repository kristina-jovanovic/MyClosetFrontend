(ns my-closet-frontend.routes
  (:require
   [bidi.bidi :as bidi]
   [pushy.core :as pushy]
   [re-frame.core :as re-frame]
   [my-closet-frontend.events :as events]))

(defmulti panels identity)
(defmethod panels :default [] [:div "No panel found for this route."])

(def routes
  (atom
    ["/" {""      :home
          "about" :about
          "get-recommendation" :get-recommendation
          "recommendations" :recommendations
          "liked-combinations" :liked-combinations
          "favorites" :favorites
          "my-clothes" :my-clothes}]))

;(def routes
;  ["/"
;   [["" :home]
;    ["about" :about]
;    ["get-recommendation" :get-recommendation]
;    ["recommendations" :recommendations]
;    ["liked-combinations" :liked-combinations]
;    ["favorites" :favorites]
;    ["my-clothes" :my-clothes]]])


(defn parse
  [url]
  (bidi/match-route @routes url))

(defn url-for
  [& args]
  (apply bidi/path-for (into [@routes] args)))

(defn dispatch
  [route]
  (let [panel (keyword (str (name (:handler route)) "-panel"))]
    (re-frame/dispatch [::events/set-active-panel panel])))

(defonce history
  (pushy/pushy dispatch parse))

(defn navigate! [handler]
      (let [url (apply url-for handler)] ;handler je npr [:liked-combinations]
           ;(js/console.log "navigating to handler:" handler "URL:" url)
           (when url
                 (pushy/set-token! history url))))

(defn start!
  []
  (pushy/start! history))

(re-frame/reg-fx
  :navigate
  (fn [handler]
    (navigate! handler)))
