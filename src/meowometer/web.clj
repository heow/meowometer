(ns meowometer.web
  (:require [ring.adapter.jetty             :as jetty]
            [compojure.core                 :as ww]
            [ring.middleware.cookies        :as mw-cookies]
            [ring.middleware.params         :as mw-params]
            [ring.middleware.keyword-params :as mw-kwparams]
            [clojure.contrib.sql            :as sql]
            [clojureql.core                 :as cql]))

(def db {:classname "com.mysql.jdbc.Driver"
         :subprotocol "mysql"
         :subname "//XYZZY.compute-1.amazonaws.com/XYZZY"
         :user "*USER*"
         :password "*PASSWORD*"})

(defn make-saying []
  (let [sayings [
                 "<em>Life is 10% of what happens to me and 90% of how I react to it.</em> - John Maxwell"
                 "<em>What lies behind us and what lies before us are tiny matters compared to what lies within us.</em> - Ralph Waldo Emerso"
                 "<em>There isn't a person anywhere who isn't capable of doing more than he thinks he can.</em> - Henry Ford"
                 "<em>We must become the change we want to see.</em> - Mahatma Gandhi"
                 "<em>And as we let our own light shine, we unconsciously give other people permission to do the same. </em> - Nelson Mandela"
                 "<em>Build a better mousetrap and the world will beat a path to your door.</em> - Ralph Waldo Emerson"
                 "<em>And in the end it's not the years in your life that count. It's the life in your years.</em> - Abraham Lincoln"
                 "<em>While we pursue happiness, we flee from contentment.</em> - Hasidic Proverb"
                 "<em>Happiness itself does not stay -- only moments of happiness do.</em> - Spanish Proverb "
                 "<em>The purpose of life is not to be happy – but to matter, to be productive, to be useful, to have it make some difference that you have lived at all.</em> - Leo Roste"
                 "<em>It is not length of life, but depth of life.</em> - Ralph Waldo Emerso"
                 "Happiness is not having what you want, but wanting what you have."
                 "<em>May you live all the days of your life.</em> - Jonathan Swift "
                 "<em>Happiness is a state of activity.</em> - Aristotle"
                 "<em>A man is not old until regrets start taking place of dreams.</em>"
                 "<em>Man is not made for defeat. A man can be destroyed, but not defeated.</em> - Ernest Hemingway"
                 "The road to success is lined with many tempting parking spaces."
                 "<em>Real success is finding your lifework in the work that you love.</em> - David McCulloug"
                 "<em>Success usually comes to those who are too busy to be looking for it.</em> - Henry David Thoreau "
                 "<em>People rarely succeed unless they have fun in what they are doing.</em> - Dale Carnegie"
                 "<em>Try not to become a man of success but rather to become a man of value.</em> - Albert Einstein "
                 "<em>Some men see things the way they are and ask, \"Why?\" I dream things that never were, and ask \"Why not?\"</em> - George Bernard Shaw"
                 "<em>Vision without action is a daydream. Action without vision is a nightmare.</em> - Japanese Proverb"
                 "<em>Life is what happens to you while you're busy making other plans.</em> - John Lennon"
                 "<em>Discontent is the first step in the progress of a man or a nation.</em> -Oscar Wilde "
                 "<em>Life can only be understood backwards; but it must be lived forwards.</em> - Soren Kierkegaard"
                 "<em>A ship is safe in harbor, but that’s not what ships are for.</em> - William Shedd"
                 "<em>Many men go fishing all their lives not knowing it is not fish they are after.</em> – Henry David Thoreau"
                 "<em>Better to light one small candle than to curse the darkness.</em> - Chinese Proverb"
                 "<em>Better to do something imperfectly than to do nothing flawlessly.</em> - Robert Schuller "
                 "<em>The way to succeed is to double your error rate.</em> - Thomas J. Watson"
                 "<em>As long as you live, keep learning how to live</em> - Seneca"
                 "<em>If there's anything more important than my ego around, I want it caught and shot now.</em> - Zaphod Beeblebrox"
                 "<em>I checked it very thoroughly, and that quite definitely is the answer. I think the problem, to be quite honest with you, is that you've never actually known what the question is.</em> - Deep Thought"
                 "<em>The chances of finding out what's really going on in the universe are so remote, the only thing to do is hang the sense of it and keep yourself occupied.</em> - S. Bardfast"
                 "<em>There is a theory which states that if ever anyone discovers exactly what the Universe is for and why it is here, it will instantly disappear and be replaced by something even more bizarre and inexplicable. There is another theory which states that this has already happened.</em> - Restaurant at the End of the Universe"
                 "<em>The fact that we live at the bottom of a deep gravity well, on the surface of a gas covered planet going around a nuclear fireball 90 million miles away and think this to be normal is obviously some indication of how skewed our perspective tends to be.</em> - Douglas Adams"
                 ]]
    (nth sayings (rand (count sayings))) ))

(defn main-page [cookies]
  (let [input-name (if (empty? (:value (cookies "name")))
                     "name: <input type='text' name='name' class='name' maxlength='10' /><br>"
                     "")]
    (str "<html><head><meta name='viewport' content='width=device-width, initial-scale=1, maximum-scale=1'> <style type='text/css'>  body { background-image:url('http://www.hdwallpapers4iphone.com/_ph/2/133108891.jpg') } input {  padding: 0.5em; width:2em;    font-size:3em;  font-weight:bold;  }  .note {  padding: 0.1em;     width: 18em; font-size:1em;  font-weight:bold;  } .name {  padding: 0.1em;     width: 14.6em; font-size:1em;  font-weight:bold;  }  </style></head><body><center><h3>How Happy Are You?</h3><form method='post' action='/'>" input-name "<input type='text' name='note' class='note' maxlength='256' /><br><input type='submit' name='value' value='1' /><input type='submit' name='value' value='2' /><input type='submit' name='value' value='3' /><br><input type='submit' name='value' value='4' /><input type='submit' name='value' value='5' /><input type='submit' name='value' value='6' /><br><input type='submit' name='value' value='7' /><input type='submit' name='value' value='8' /><input type='submit' name='value' value='9' /></form></center></body></html>") ))

(defn process-form [params cookies]
  (let [value (:value params)
        note  (:note params)
        name  (if (not (empty? (:name params)))
                (:name params)
                (:value (cookies "name")))]

    ;; debug
    (println "person: " name " value: " value " note:  " note)

    ;; don't save anything with test
    (if (> 0 (.indexOf (.toLowerCase note) "test")) 
      (sql/with-connection db
        (cql/conj!
         (cql/table :happiness) {:person name :value value :note note})))

    ;; set cookie, return html
    {:cookies {"name" name}
     :body (str "<html><head><meta HTTP-EQUIV='REFRESH' content='10; url='/'\"</head><body><h1>" (make-saying) "</h1></body></html>")}))
  
(ww/defroutes routes
  (ww/POST "/" {params :params cookies :cookies} (process-form params cookies))
  (ww/GET  "/" {cookies :cookies}                (main-page cookies)))

(def app
     (-> #'routes
         mw-cookies/wrap-cookies
         mw-kwparams/wrap-keyword-params
         mw-params/wrap-params))

(defn -main []
  (let [port (if (nil? (System/getenv "PORT")) 
               8000 ; localhost
               (Integer/parseInt (System/getenv "PORT")))] ; heroku
    (jetty/run-jetty app {:port port})) )