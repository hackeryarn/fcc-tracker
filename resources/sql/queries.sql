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

-- :name create-member! :! :n
-- create a new member
INSERT INTO members
(organization, fcc_username, name)
VALUES (:organization, :fcc_username, :name)

-- :name list-members
-- selects members from the given organization
SELECT fcc_username, name FROM members
WHERE organization = :organization

-- :name delete-member! :! :n
-- deletes the member with the give username and org
DELETE FROM members
WHERE fcc_username = :fcc_username
AND organization = :organization
