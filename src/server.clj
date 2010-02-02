(ns server
  (:gen-class)
  (:use compojure
	somnium.congomongo
	helpers
	date-helpers
	auth
	journal-pages
	auth-pages
	user-pages
	visualizations))

(mongo! :db "physiq")

(defroutes physiq-routes
  (GET "/"
       (redirect-to "/journal/"))
  (GET "/login/"
       (auth-login-page))
  (POST "/login/"
	(auth-post-login session (params :email) (params :password)))
  (GET "/logout/"
	(only-authed (auth-post-logout session)))
  (GET "/user/new"
       (user-new-page))
  (GET "/journal/"
       (redirect-to (str "/journal/" (format-ymd (today)))))
  (GET "/journal/:date"
       (only-authed (journal-home-page (request :auth-user) (params :date))))
  (PUT "/journal/:date"
	(only-authed (journal-put-stats (request :auth-user) params)))
  (POST "/journal/:date/food"
	(only-authed (journal-post-food (request :auth-user) params)))
  (GET "/charts/weight-over-plan"
       (only-authed (serve-chart request (chart-weight-over-plan (request :auth-user)))))
  (GET "/*"
       (or (serve-file (params :*)) :next))
  (ANY "*" 
       (page-not-found)))

(decorate physiq-routes (with-user) (with-session))

(defserver physiq-server {:port 8080}
  "/*" (servlet physiq-routes))

(defn -main [& args]
  (start physiq-server))