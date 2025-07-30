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
(re-frame/reg-sub
  ::clothes
  (fn [db _]
      (:clothes db)))

;recommendations
(re-frame/reg-sub
  ::combinations
  (fn [db _]
      (:combinations db)))

;feedback-message
(re-frame/reg-sub
  ::feedback-message
  (fn [db _]
      (:feedback-message db)))