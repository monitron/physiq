(ns user
  (:use somnium.congomongo
	helpers)
  (:import java.util.Random))

(defstruct user :email)  

(defn create-user!
  "Add a new user to the database. Password cannot be specified here."
  [user-details]
  (insert! :users user-details))

(defn user-with-email
  "Find a user with the given email"
  [email]
  (fetch-one :users :where {:email email}))

(defn user-with-id
  "Find a user with the given Object ID"
  [id]
  (fetch-one :users :where {:_id id}))

(defn generate-salt
  "Generate a random password salt"
  []
  (Integer/toHexString (.nextInt (Random.) 65535)))

(defn encrypt-password
  "Encrypt a password for storage or comparison with stored password"
  [plaintext salt]
  (sha (str plaintext salt)))

(defn set-password!
  "Sets the password for a user."
  [user password]
  (let [salt (generate-salt)
	encrypted-pw (encrypt-password password salt)]
    (update! :users user {:$set {:salt salt :password encrypted-pw}})))

(defn password-matches?
  "Check a password against a user's encrypted password"
  [{encrypted-pw :password salt :salt} candidate-pw]
  (= encrypted-pw (encrypt-password candidate-pw salt)))
