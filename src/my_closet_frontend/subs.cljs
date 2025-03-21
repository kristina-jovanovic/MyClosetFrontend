(ns my-closet-frontend.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
 ::active-panel
 (fn [db _]
   (:active-panel db)))

;my-clothes
;; Define a re-frame subscription to get clothes from the app state
(re-frame/reg-sub
  ::clothes
  (fn [db _]
      (:clothes db)))