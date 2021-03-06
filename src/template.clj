(ns template
  (:use compojure))

(defn page
  [user title & content]
  (html
   [:html
    [:head 
     [:meta {:name "viewport" :content "width=320; initial-scale: 1.0; user-scalable: no"}]
     [:title title]
     (include-js "/jquery.js" "/application.js")
     (include-css "/application.css")]
    [:body
     (if user
     [:div#auth
      "Physiq for " (user :email) " "
      (link-to "/logout/" "logout")])
     [:h1 title]
     content]]))