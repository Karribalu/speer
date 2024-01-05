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
<span>It is cloud version of MongoDB integrated with Elastic Search, so you dont have worry about setting it up in local.</span>
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
<li>Hurray! Your MongoDB cluster is ready.</li>
</ol>
<br>
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
