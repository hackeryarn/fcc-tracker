-- :name create-org! :! :n
-- :doc creates a new organization record
INSERT INTO organizations
(id, pass)
VALUES (:id, :pass)

-- :name get-org :? :1
-- :doc retrieve a organization given the id.
SELECT * FROM organizations
WHERE id = :id

-- :name delete-org! :! :n
-- :doc delete a organization given the id
DELETE FROM organizations
WHERE id = :id
