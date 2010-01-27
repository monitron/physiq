(ns server
  (:gen-class)
  (:use compojure
	somnium.congomongo
	helpers
	date-helpers
	journal-pages
	visualizations))

(mongo! :db "physiq")

(defroutes physiq-routes
  (GET "/"
       (html [:h1 "Hello World"]))
  (GET "/journal/"
       (redirect-to (str "/journal/" (format-ymd (today)))))
  (GET "/journal/:date"
       (journal-home-page (params :date)))
  (PUT "/journal/:date"
	(journal-put-stats params))
  (POST "/journal/:date/food"
	(journal-post-food params))
  (GET "/charts/weight-over-plan"
       (serve-chart request (chart-weight-over-plan)))
  (GET "/*"
       (or (serve-file (params :*)) :next)) 
  (ANY "*" 
       (page-not-found)))

(defserver physiq-server {:port 8080}
  "/*" (servlet physiq-routes))

(defn -main [& args]
  (start physiq-server))