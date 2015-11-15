(ns meowometer.notify
  (:require [clj-aws.core    :as aws]
            [clj-aws.sns     :as sns]
            [clj-time.core   :as t]
            [clj-time.format :as fmt]))

(defn make-saying []
  (let [sayings ["" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""
                 "make meowometer happy"
                 "meowometer ask: how happy are you?"
                 "feed meowometer"
                 "a meowometer entry a day makes you rich"
                 "meowometer wonders if you're bored"
                 "meowometer builds character ...and a great data profile!"
                 "when I die, my life with be meowometered"
                 "an examined life can be found meowometered"
                 "you only live once, share it with meowometer"
                 "meowometer say: life is an onion, it makes computers weep"
                 "meowometer loves you"
                 "the purpose of life is to share it with meowometer"
                 "may you meowometer all the days of your life"
                 "the most important things in life should be in meowometer"
                 "meowometer diem!"
                 "life is what happens between meowometer entries"
                 "find yourself in meowometer"
                 "where there is love, there is meowometer"
                 "life is to be meowometered, not endured"
                 "life can only be understood backwards; but it must be meowometered forwards"
                 "that it will never come again is what makes this meowometer entry so sweet"
                 "don't be afraid of death; be afraid of an unmeowometered life"
                 "meowometer warn: this is your life and its ending one moment at a time"
                 "the most wasted of all days is one without meowometer"
                 "the things you do for yourself are gone when you are gone, but the things in meowometer remain as your legacy"
                 "life is a meowometeed adventure or nothing at all"
                 "nothing is to be feared, it is only to be meowometered"
                 "how much do I love thee?  let me meowometer the ways"
                 "meowometer is your friend"
                 ]]
    (nth sayings (rand (count sayings))) ))

(defn send-message[msg]
  (sns/publish (sns/client (aws/credentials "XXX" "XXX"))
               "arn:aws:sns:us-east-1:XXX:XXX"
               msg))

(defn randomly-notify [nums]
  (let [now-hour (Integer/parseInt (fmt/unparse (fmt/formatter "HH" (t/time-zone-for-id "America/New_York"))
                                                (t/now)))]
    (cond (< now-hour 9)           (println "too early")
          (> now-hour 22)          (println "too late")
          (not= 0 (rand-int nums)) (println "no dice")
          :else (send-message (str "http://meowometer.com  \n\n" (make-saying))) )))

(defn -main []
  (randomly-notify (* 42 5))) ; 6 per hour * 14 hours = 42 per day