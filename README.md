<h3>A Backend app for Notes App</h3>
<h5>Technologies used </h5>
<ul>
<li>Spring Boot</li>
<li>jjwt library for JWT authorization</li>
<li>bucket4j library for rate limiting</li>
<desc>bucket4J gives a predefined functions to implement rate limiting on REST APIs</desc>
<li>MongoDB Atlas</li>
<desc>I have used MongoDB Atlas professionally and I know what it is capable of, The reason I have used this is it uses apache lucene search internally which makes serches very fast given that we create search-index on teh fields we need to search against.</desc>
<li>Junit, Mockito for Tests</li>
</ul>

<h4>Dependencies</h4>
<ul>
<li>Java - 19.0.2</li>
<li>Maven - 3.9.5</li>
<li>MongoDB Atlas Cluster (Will explain in detail)</li>
</ul>


<h4>Steps to run tests </h4>
<ol>
<li>Change the current directory to the repo and run.</li>
<li><i>mvn test</i></li>
<desc>It runs all the tests</desc>
<li>If you want to run a single test</li>
<li><i>mvn test -Dtest="test_file_name"</i></li>
</ol>

<h3>Configuration setup</h3>
We need to add some application properties before we start executing.

<h4>MongoDB Cluster</h4>
<span>We are using MongoDB atlas for this assignment.</span><br>
<span>It is cloud version of MongoDB integrated with Elasticsearch, so you dont have worry about setting it up in local.</span>
<h5>Steps to setup the MongoDB cluster and index creation</h5>
<ol>
<li>Create a free MongoDB Cluster following these steps <br/><a>https://www.mongodb.com/basics/clusters/mongodb-cluster-setup#:~:text=about%20storage%20capacity.-,Creating%20a%20MongoDB%20Cluster,-Depending%20on%20your</a></li>
<li>Now you have the cluster created we will create user, please note the password somewhere</li>
<img src="readme-photos/user.png"/>
<li>click on finish at the last after scrolling down</li>
<img src="readme-photos/home.png"/>
<li>Now that your user creation is done, click on browse collections</li>
<img src="readme-photos/collections.png"/>
<li>click on Add My Own Data.</li>
<li>Please enter your desired database name and in collections please enter <i>users</i></li>
<li>Also create a collection for <i>notes</i> following the above steps, no data needed</li>
<li>Hurray! Your MongoDB cluster is ready.</li>
</ol>
<br>

<h4>MongoDB Search Index Creation </h4>
<p>We are using search index to provide faster responses for content search in the notes.</p>
<h5>Steps for creating the index</h5>
<ol>
<li>Given that you created the cluster and databases</li>
<li>Please open Atlas search option after browsing collection </li>
<img src="readme-photos/browse.png"/>
<li>Follow the following steps, Please name your index as <i>search-index</i></li>
<img src="readme-photos/index1.png"/>
<img src="readme-photos/index2.png"/>
<li>After these steps please click on create search index button</li>
<li>Your index is created, and now we wait till the index creation gets into Active state like this</li>
<img src="readme-photos/active.png"/>
<li>We are using dynamic field mappings, so we don't need to add field mappings for indexes </li>
</ol>

<h4>Configuring application.yml for our app.</h4>
<ol>
<li>We need MongoDB cluster connection string to make a connection between our app and your cluster.</li>
<li>You can find connect option in the Home page</li>
<img src="readme-photos/home.png"/>
<li>Select Drivers after that, you should see something similar</li>
<img src="readme-photos/connection_string.png"/>
<li>Copy the string and replace username and password with your credentials.</li>
<li>Please create a yml file in the folder where you can find <i>application.sample.yml</i> named <i>application.yml</i></li>
<li>Copy paste the contents from <i>application.sample.yml</i> to <i>application.yml</i></li>
<li>Replace MongoDB connection string with your string and database name.</li>
<li>I have set rate_limit_per_minute to 10 to limit the hits to apis per minute, feel free to change and experiment</li>
<li>Hurray!, we are done with the configuration for our app.</li>
</ol>
<br/>
<br/>


<h3>Steps to run the app </h3>
<ol>
<li>Change the current directory to the repo and run.</li>
<li><i>mvn clean install</i></li>
<desc>This will create a jar build which we can run using java.</desc>
<li>run the following command to start execution</li>
<li><i>java -jar target/notes-0.0.1-SNAPSHOT.jar</i></li>
<li>Booom! your application is started.</li>
</ol>


<h3> Example Curl commands for all APIs </h3>
<h4>Authentication</h4>
<ol>
<li>POST /api/auth/signup</li>

``` 
curl -X POST -H "Content-Type: application/json" \
-H "Accept: application/json" \
-d '{"username":"test", "email":"test@gmail.com", "password": "securepassword"}' \
http://localhost:8080/api/auth/signup 
```

<h5>Response for Signup </h5>

```
{
    "message": "User Registered Successfully",
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0MiIsImV4cCI6MTcwNDY2NDk4OX0.lOYKDP7kQxGTIuSU2klfOaxjo0JONv4hOn--tP6f5Hw"
}
``` 

<br>
<li>POST /api/auth/login</li>

```
curl -X POST -H "Content-Type: application/json" \
-H "Accept: application/json" \
-d '{"username":"test", "password": "securepassword"}' \
http://localhost:8080/api/auth/login
```

<h5>Response for login </h5>

```
{
    "message": "User Logged in Successfully",
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0MiIsImV4cCI6MTcwNDY2NDk4OX0.lOYKDP7kQxGTIuSU2klfOaxjo0JONv4hOn--tP6f5Hw"
}
``` 

<br>

<li>GET /api/notes: get a list of all notes for the authenticated user.</li>

```
curl -X GET \
-H "Content-Type: application/json" \
-H "Accept: application/json" \
-H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0IiwiZXhwIjoxNzA0NTUwMzA3fQ.o_oP_lQ3h1y4jISsLr6rlm7DyiRndVGPQ7R_jdGn-Cw" \
http://localhost:8080/api/notes/

```
<h5>Response for Get All notes </h5>

```
[
    {
        "id": "6599cebc4b6c940d9d3f2931",
        "username": "test2",
        "title": "test title",
        "content": "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Amet consequatur, unde cupiditate dolorum, soluta doloribus aperiam alias tempora id optio odit corrupti eum consequuntur natus a officiis quidem eaque dolorem eos numquam. Veniam reiciendis alias aperiam facilis impedit nesciunt optio quae quisquam perferendis, iure, quod voluptates eveniet rerum culpa. Eligendi soluta, quis nihil similique sequi, voluptates consectetur sed laudantium sit numquam commodi ea iusto laborum quod culpa corrupti tenetur possimus illum inventore. Corporis, odio temporibus quos eveniet velit quas incidunt fugit nulla architecto vitae aut vel nam dolore sequi ad aspernatur corrupti reprehenderit debitis cumque commodi adipisci! Reiciendis, corporis asperiores.",
        "sharedList": [],
        "createdAt": "2024-01-06T22:05:48.037",
        "updatedAt": "2024-01-06T22:05:48.038"
    },
    {
        "id": "6599ced74b6c940d9d3f2932",
        "username": "test2",
        "title": "test title",
        "content": "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Amet consequatur, unde cupiditate dolorum, soluta doloribus aperiam alias tempora id optio odit corrupti eum consequuntur natus a officiis quidem eaque dolorem eos numquam. Veniam reiciendis alias aperiam facilis impedit nesciunt optio quae quisquam perferendis, iure, quod voluptates eveniet rerum culpa. Eligendi soluta, quis nihil similique sequi, voluptates consectetur sed laudantium sit numquam commodi ea iusto laborum quod culpa corrupti tenetur possimus illum inventore. Corporis, odio temporibus quos eveniet velit quas incidunt fugit nulla architecto vitae aut vel nam dolore sequi ad aspernatur corrupti reprehenderit debitis cumque commodi adipisci! Reiciendis, corporis asperiores.",
        "sharedList": [],
        "createdAt": "2024-01-06T22:06:15.209",
        "updatedAt": "2024-01-06T22:06:15.209"
    }
]
``` 

<br>

<li>GET /api/notes/:id: get a note by ID for the authenticated user.</li>

```
curl -X GET \
-H "Content-Type: application/json" \
-H "Accept: application/json" \
-H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0IiwiZXhwIjoxNzA0NTUwMzA3fQ.o_oP_lQ3h1y4jISsLr6rlm7DyiRndVGPQ7R_jdGn-Cw" \
http://localhost:8080/api/notes/6599cebc4b6c940d9d3f2931

```
<h5>Response for Get notes by ID </h5>

```
[
    {
        "id": "6599cebc4b6c940d9d3f2931",
        "username": "test2",
        "title": "test title",
        "content": "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Amet consequatur, unde cupiditate dolorum, soluta doloribus aperiam alias tempora id optio odit corrupti eum consequuntur natus a officiis quidem eaque dolorem eos numquam. Veniam reiciendis alias aperiam facilis impedit nesciunt optio quae quisquam perferendis, iure, quod voluptates eveniet rerum culpa. Eligendi soluta, quis nihil similique sequi, voluptates consectetur sed laudantium sit numquam commodi ea iusto laborum quod culpa corrupti tenetur possimus illum inventore. Corporis, odio temporibus quos eveniet velit quas incidunt fugit nulla architecto vitae aut vel nam dolore sequi ad aspernatur corrupti reprehenderit debitis cumque commodi adipisci! Reiciendis, corporis asperiores.",
        "sharedList": [],
        "createdAt": "2024-01-06T22:05:48.037",
        "updatedAt": "2024-01-06T22:05:48.038"
    }
]
``` 

<br>
<li>POST /api/notes: create a new note for the authenticated user.</li>

```
curl -X POST \
-H "Content-Type: application/json" \
-H "Accept: application/json" \
-H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0IiwiZXhwIjoxNzA0NTUwMzA3fQ.o_oP_lQ3h1y4jISsLr6rlm7DyiRndVGPQ7R_jdGn-Cw" \
-d '{"title":"test title", "content": "test content"}' \
http://localhost:8080/api/notes/

```
<h5>Response for create Notes</h5>

```
{
    "message": "Notes created successfully",
    "notes": {
        "id": "6599ced94b6c940d9d3f2934",
        "username": "test2",
        "title": "test title",
        "content": "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Amet consequatur, unde cupiditate dolorum, soluta doloribus aperiam alias tempora id optio odit corrupti eum consequuntur natus a officiis quidem eaque dolorem eos numquam. Veniam reiciendis alias aperiam facilis impedit nesciunt optio quae quisquam perferendis, iure, quod voluptates eveniet rerum culpa. Eligendi soluta, quis nihil similique sequi, voluptates consectetur sed laudantium sit numquam commodi ea iusto laborum quod culpa corrupti tenetur possimus illum inventore. Corporis, odio temporibus quos eveniet velit quas incidunt fugit nulla architecto vitae aut vel nam dolore sequi ad aspernatur corrupti reprehenderit debitis cumque commodi adipisci! Reiciendis, corporis asperiores.",
        "sharedList": [],
        "createdAt": "2024-01-06T22:06:17.536109",
        "updatedAt": "2024-01-06T22:06:17.536135"
    }
}
``` 

<br>
<li>PUT /api/notes/testId update an existing note by ID for the authenticated user.</li>

```
curl -X PUT \
-H "Content-Type: application/json" \
-H "Accept: application/json" \
-H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0IiwiZXhwIjoxNzA0NTUwMzA3fQ.o_oP_lQ3h1y4jISsLr6rlm7DyiRndVGPQ7R_jdGn-Cw" \
-d '{"title":"test title", "content": "test content"}' \
http://localhost:8080/api/notes/testId

```

<h5>Response for Update Notes</h5>

```
{
    "message": "Document updated successfully",
    "notes": {
        "id": "6599ce414b6c940d9d3f2930",
        "username": "test2",
        "title": "test title",
        "content": "test title",
        "sharedList": [],
        "createdAt": "2024-01-06T22:03:45.598",
        "updatedAt": "2024-01-06T22:11:20.878237"
    }
}
``` 

<br>
<li>DELETE /api/notes/testId delete a note by ID for the authenticated user.</li>

```
curl -X DELETE \
-H "Content-Type: application/json" \
-H "Accept: application/json" \
-H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0IiwiZXhwIjoxNzA0NTUwMzA3fQ.o_oP_lQ3h1y4jISsLr6rlm7DyiRndVGPQ7R_jdGn-Cw" \
http://localhost:8080/api/notes/testId

```

<h5>Response for Delete Notes</h5>

```
{
    "message": "Document deleted successfully",
    "notes": {
        "id": "6599ce414b6c940d9d3f2930",
        "username": "test2",
        "title": "test title",
        "content": "test title",
        "sharedList": [],
        "createdAt": "2024-01-06T22:03:45.598",
        "updatedAt": "2024-01-06T22:11:20.878"
    }
}
``` 

<br>

<li>POST /api/notes/testId/share  share a note with another user for the authenticated user.</li>
the user you are sharing can only access the note by hitting the GET /api/notes/:id, but he will not get the notes when searches for all of his notes.

```
curl -X POST \
-H "Content-Type: application/json" \
-H "Accept: application/json" \
-H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0IiwiZXhwIjoxNzA0NTUwMzA3fQ.o_oP_lQ3h1y4jISsLr6rlm7DyiRndVGPQ7R_jdGn-Cw" \
-d '{"username":"test"}' \
http://localhost:8080/api/notes/testId/share

```

<h5>Response for share Notes</h5>

```
{
    "message": "Note having id 6599cebc4b6c940d9d3f2931is shared with user test",
    "notes": {
        "id": "6599cebc4b6c940d9d3f2931",
        "username": "test2",
        "title": "test title",
        "content": "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Amet consequatur, unde cupiditate dolorum, soluta doloribus aperiam alias tempora id optio odit corrupti eum consequuntur natus a officiis quidem eaque dolorem eos numquam. Veniam reiciendis alias aperiam facilis impedit nesciunt optio quae quisquam perferendis, iure, quod voluptates eveniet rerum culpa. Eligendi soluta, quis nihil similique sequi, voluptates consectetur sed laudantium sit numquam commodi ea iusto laborum quod culpa corrupti tenetur possimus illum inventore. Corporis, odio temporibus quos eveniet velit quas incidunt fugit nulla architecto vitae aut vel nam dolore sequi ad aspernatur corrupti reprehenderit debitis cumque commodi adipisci! Reiciendis, corporis asperiores.",
        "sharedList": [
            "test"
        ],
        "createdAt": "2024-01-06T22:05:48.037",
        "updatedAt": "2024-01-06T22:12:49.499899"
    }
}
``` 

<br>
<li>GET /api/search?q=:query: search for notes based on keywords for the authenticated user. </li>
We can add as many keywords as we want, : (colon) split, it will take as an OR query.
This query will also give highlights and its surrounding text.

```
curl -X GET \
-H "Content-Type: application/json" \
-H "Accept: application/json" \
-H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0IiwiZXhwIjoxNzA0NTUwMzA3fQ.o_oP_lQ3h1y4jISsLr6rlm7DyiRndVGPQ7R_jdGn-Cw" \
http://localhost:8080/api/search?q=lorem:ipsum

```

<h5>Response for Text Search </h5>

```
   {
        "_id": "6599ced94b6c940d9d3f2934",
        "username": "test2",
        "title": "test title",
        "content": "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Amet consequatur, unde cupiditate dolorum, soluta doloribus aperiam alias tempora id optio odit corrupti eum consequuntur natus a officiis quidem eaque dolorem eos numquam. Veniam reiciendis alias aperiam facilis impedit nesciunt optio quae quisquam perferendis, iure, quod voluptates eveniet rerum culpa. Eligendi soluta, quis nihil similique sequi, voluptates consectetur sed laudantium sit numquam commodi ea iusto laborum quod culpa corrupti tenetur possimus illum inventore. Corporis, odio temporibus quos eveniet velit quas incidunt fugit nulla architecto vitae aut vel nam dolore sequi ad aspernatur corrupti reprehenderit debitis cumque commodi adipisci! Reiciendis, corporis asperiores.",
        "createdAt": "2024-01-06T22:06:17.536+00:00",
        "updatedAt": "2024-01-06T22:06:17.536+00:00",
        "highlights": [
            {
                "score": 5.81119966506958,
                "path": "content",
                "texts": [
                    {
                        "value": "Lorem",
                        "type": "hit"
                    },
                    {
                        "value": " ",
                        "type": "text"
                    },
                    {
                        "value": "ipsum",
                        "type": "hit"
                    },
                    {
                        "value": " dolor sit amet, consectetur adipisicing elit. ",
                        "type": "text"
                    }
                ]
            }
        ]
    }

```

</ol>
