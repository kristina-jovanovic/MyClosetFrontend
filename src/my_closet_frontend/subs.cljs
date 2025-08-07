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

;last-feedback-opinion
(re-frame/reg-sub
  ::last-feedback-opinion
  (fn [db _]
      (:last-feedback-opinion db)))

;liked-combinations
(re-frame/reg-sub
  ::liked-combinations
  (fn [db _]
      (:liked-combinations db)))

;favorite-combinations
(re-frame/reg-sub
  ::favorite-combinations
  (fn [db _]
      (:favorite-combinations db)))

;users
(re-frame/reg-sub
  ::users
  (fn [db _] (:users db)))

(re-frame/reg-sub
  ::current-user-id
  (fn [db _] (:current-user-id db)))

(re-frame/reg-sub
  ::current-user
  :<- [::users]
  :<- [::current-user-id]
  (fn [[users current-id] _]
      (some #(when (= (:user-id %) current-id) %) users)))