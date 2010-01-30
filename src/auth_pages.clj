(ns auth-pages
  (:use compojure
	template
	auth))

(defn auth-login-page
  []
  (page
   nil
   "Log In"
   [:div.section
    (form-to [:post "/login/"]
	     [:ol.forms
	      [:li (label "email" "E-mail:") 
	       (text-field "email")]
	      [:li (label "password" "Password:") 
	       (password-field "password")]
	      [:li (submit-button "Log In")]])]))

(defn auth-post-login
  [session email password]
  (let [user (authenticate email password)]
    (if (nil? user)
      (redirect-to "/login/")
      [302 {:headers {"Location" "/"}
	    :session (assoc session :user-id (:_id user))}])))

(defn auth-post-logout
  [session]
  [302 {:headers {"Location" "/"}
	:session (assoc session :user-id nil)}])