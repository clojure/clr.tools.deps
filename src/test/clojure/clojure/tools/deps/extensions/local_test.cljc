;   Copyright (c) Rich Hickey. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns clojure.tools.deps.extensions.local-test
  (:require
    [clojure.test :refer [deftest is are]]
    [clojure.tools.deps.extensions :as ext]
    [clojure.tools.deps.extensions.local :as local])
  (:import
    [clojure.lang ExceptionInfo]
	[System.IO Path File Directory FileInfo FileAttributes]))

(defn- create-temp-directory []
   (let [temp-path (Path/GetTempPath)
         filename  (.ToString (Guid/NewGuid) "N")
		 dir-info  (Directory/CreateDirectory (Path/Combine temp-path filename))
		 dir-name (.FullName dir-info)]
	  dir-name))

(defn- create-temp-archive-directory []
   (let [dir-name (create-temp-directory)]
	  (File/SetAttributes dir-name (enum-or (File/GetAttributes dir-name) FileAttributes/Archive))
	  dir-name))
	     
(defn- create-temp-file []
  (let [dir-name (create-temp-directory)
        file-name (.ToString (Guid/NewGuid) "N")
		path (Path/Combine dir-name file-name)]
    (File/WriteAllText path "")
	path))



(deftest local-manifest-type-on-an-archive-directory-returns-deps 
  (let [dir (create-temp-archive-directory)]
     (try
	   (File/WriteAllText (Path/Combine dir "deps.edn") "{}")   ;; needs a file in the directory
	   (is (= {:deps/manifest :deps, :deps/root dir}(ext/manifest-type 'r/r {:local/root dir} nil)))	   
	   (finally
		 (Directory/Delete dir true)))))
		 
(deftest local-manifest-type-on-file
  (let [file (create-temp-file)
        fi (FileInfo. file)]
    (try
	  (is (thrown-with-msg?	ExceptionInfo #"^Local root .* must be a directory" (#'local/ensure-directory 'r/r file)))
	  (finally 
	     (let [dir (FileInfo/.DirectoryName fi)]
		   (Directory/Delete dir true))))))
		