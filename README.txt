### Milestone API for making users and executing searchs

#### Valid Routes

- /users

   - Gets a list of all users that have been created and their information

- /ping

   - Returns "Pong" to ensure that service is up

- /create_user

   - POST request that takes in a username and password and creates a new useRegex

- /change_password

   - POST request that sends the username and the old and new password and then UpdatePasswordInfo

- /search?q=

   - POST request that takes in a username and password to add to the specific user
   - This search returns the results of the term passed in the url

- /search_terms GET request

   -this returns a list of all unique search terms

- /search_terms POST request

   - this returns a list of all unique search terms for a user passed in

- /most_common_search  GET request

   - this returns the most common search over all Users

- /most_common_search POST request

   - POST request this returns the most common search for the user information passed in
