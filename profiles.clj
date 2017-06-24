;; WARNING
;; The profiles.clj file is used for local environment variables, such as database credentials.
;; This file is listed in .gitignore and will be excluded from version control by Git.

{:profiles/dev  {:env {:database-url "postgresql://localhost/fcc_tracker_dev?user=postgres&password=postgres"}}
 :profiles/test {:env {:database-url "postgresql://localhost/fcc_tracker_test?user=postgres&password=postgres"}}}
