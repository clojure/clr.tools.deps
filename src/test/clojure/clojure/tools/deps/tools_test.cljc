(ns clojure.tools.deps.tools-test
  (:require
    [clojure.test :refer [deftest is are testing]]
    [clojure.tools.deps.tool :as tool])
  (:import
    #?(:cljr [System.IO FileInfo DirectoryInfo Directory File Path])))
       
#?(
:cljr

(defmacro bind-environment-variable [[env-var new-val] & body]
  `(let [var# ~env-var
         val# ~new-val
         current# (Environment/GetEnvironmentVariable var#)]
      (Environment/SetEnvironmentVariable var# val#)
      (try
        ~@body
        (finally 
           (Environment/SetEnvironmentVariable var# current#)))))
           
)


#?(
:cljr

(deftest list-tools-test
  ;; negative test
  (let [tempdir (Path/Combine (Path/GetTempPath) (.ToString (Guid/NewGuid) "N"))]
    (Directory/CreateDirectory tempdir)
    (try 
      (bind-environment-variable ["CLJ_CONFIG" tempdir]
        (is (= () (tool/list-tools))))
      (finally
        (Directory/Delete tempdir true))))
        
  ;; positive test
  (let [tempdir (Path/Combine (Path/GetTempPath) (.ToString (Guid/NewGuid) "N"))
        tooldir (Path/Combine tempdir "tools")]
    (Directory/CreateDirectory tooldir) 
    (try 
      (File/WriteAllText (Path/Combine tooldir "a.edn") "{}")
      (File/WriteAllText (Path/Combine tooldir "b.edn") "{}")
      (File/WriteAllText (Path/Combine tooldir "notes.txt") "No notes.")
      (bind-environment-variable ["CLJ_CONFIG" tempdir]
        (is (= ["a" "b"] (tool/list-tools))))
      (finally
        (Directory/Delete tempdir true)))))     
)