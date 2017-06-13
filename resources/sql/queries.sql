-- :name create-org! :! :n
-- :doc creates a new organization record
INSERT INTO organizations
(id, org_name, pass)
VALUES (:id, :org_name, :pass)

-- :name update-org! :! :n
-- :doc update an existing organizatin record
UPDATE organizations
SET org_name = :org_name
WHERE id = :id

-- :name get-org :? :1
-- :doc retrieve a organization given the id.
SELECT * FROM organizations
WHERE id = :id

-- :name delete-org! :! :n
-- :doc delete a organization given the id
DELETE FROM organizations
WHERE id = :id
