(ns user-pages
  (:use compojure
	template))


(defn user-form
  [values]
  [:ol.forms
   [:li (label "email" "E-Mail:")
    (text-field "email")]
   [:li (label "password" "Password:")
    (password-field "password")]
   [:li (label "password-confirm" "Password Again:")
    (password-field "password-confirm")]])

(defn user-new-page
  []
  (page
   nil
   "New User"
   [:div.section 
    [:p "Welcome! Please provide a few details about yourself."]
    (form-to [:post "/user/"]
	     (user-form [])
	     [:li (submit-button "Sign Up")])]))

