(ns auth
  (:use compojure
	user))

(defn authenticate
  "Given an email and password, returns the matching user or nil"
  [email password]
  (let [user (user-with-email email)]
    (cond (nil? user) nil
	  (password-matches? user password) user
	  true nil)))

(defn with-user
  "Compojure middleware to grab a user for the session"
  [handler]
  (fn [request]
    (let [user-id (:user-id (:session request))]
      (if (nil? user-id)
	(handler request)
	(handler (assoc request :auth-user (user-with-id user-id)))))))

(defmacro only-authed
  [body]
  `(if (nil? (:auth-user ~'request))
     (redirect-to "/login/")
     ~body))
		 
(defn only-authenticated
  "Compojure middleware to require authentication"
  [handler]
  (fn [request]
    (if (nil? (:auth-user request))
      {:status 302 :headers {"Location" "/login/"}}
      (handler request))))
      