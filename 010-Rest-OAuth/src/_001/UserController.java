/*
What is OAuth2
OAuth2 is an standardized authorization protocol/framework. As per Official OAuth2 Specification:
The OAuth 2.0 authorization framework enables a third-party application to obtain limited access to an HTTP service, either on behalf of a resource 
owner by orchestrating an approval interaction between the resource owner and the HTTP service, or by allowing the third-party application to obtain 
access on its own behalf.

Big players like Google, Facebook and others are already using their own OAuth2 implementations for quite some time. Enterprises too are moving fast 
towards OAuth2 adoption.

Spring Security OAuth project provides all the necessary API we might need in order to develop an OAuth2 compliant implementation using Spring. 
Official Spring security oauth project provides a comprehensive example for implementing OAuth2. The code samples of this post is inspired by that 
examples itself. The intention of this post is to just use bare-minimum functionality required in order to secure our REST API, nothing more.

At minimum, you should be aware of four key concepts in OAuth2:

----------------
1. OAuth2 Roles:
----------------
OAuth2 defines four roles:

Resource Owner: Could be you. An entity capable of granting access to a protected resource. When the resource owner is a person, it is referred to 
as an end-user.
Resource Server: The server hosting the protected resources, capable of accepting and responding to protected resource requests using access tokens.
Client: An application making protected resource requests on behalf of the resource owner and with its authorization. It could be a mobile app asking 
your permission to access your Facebook feeds, a REST client trying to access REST API, a web site [Stackoverflow e.g.] providing an alternative login 
option using Facebook account.
Authorization Aerver: The server issuing access tokens to the client after successfully authenticating the resource owner and obtaining authorization.

------------------------------------
2. OAuth2 Authorization Grant types:
------------------------------------
An authorization grant is a credential representing the resource owner�s authorization (to access its protected resources) used by the client to 
obtain an access token. The specification defines four grant types:

authorization code
implicit
resource owner password credentials
client credentials

We will be using resource owner password credentials grant type. The reason is simple, we are not implementing a view which redirects us to a login 
page. Only the usage where a client [Postman or RestTemplate based Java client e.g.] have the Resource owner�s credentials and they provide those 
credential [along with client credentials] to authorization server in order to eventually receive the access-token[and optionally refresh token], 
and then use that token to actually access the resources.

A common example is the GMail app [a client] on your smartphone which takes your credentials and use them to connect to GMail servers. It also shows 
that �Password Credentials Grant� is best suited when both the client and the servers are from same company as the trust is there, you don�t want to 
provide your credentials to a third party.

----------------
3.OAuth2 Tokens:
----------------
Tokens are implementation specific random strings, generated by the authorization server and are issued when the client requests them.

Access Token : Sent with each request, usually valid for a very short life time [an hour e.g.]
Refresh Token : Mainly used to get a new access token, not sent with each request, usually lives longer than access token.
A Word on HTTPS : For any sort of Security implementation, ranging from Basic authentication to a full fledged OAuth2 implementation, HTTPS is a must 
have. Without HTTPS, no matter what your implementation is, security is vulnerable to be compromised.

-----------------------------
4. OAuth2 Access Token Scope:
-----------------------------
Client can ask for the resource with specific access rights using scope [want to access feeds & photos of this users facebook account], and 
authorization server in turn return scope showing what access rights were actually granted to the client [Resource owner only allowed feeds access, 
no photos e.g.].


DEMO EXPLAINED:

1. Resource Server
Resource Server hosts the resources [our REST API] the client is interested in. Resources are located on /user/. @EnableResourceServer annotation, 
applied on OAuth2 Resource Servers, enables a Spring Security filter that authenticates requests using an incoming OAuth2 token. Class 
ResourceServerConfigurerAdapter implements ResourceServerConfigurer providing methods to adjust the access rules and paths that are protected by 
OAuth2 security.

2. Authorization Server
Authorization server is the one responsible for verifying credentials and if credentials are OK, providing the tokens[refresh-token as well as 
access-token]. It also contains information about registered clients and possible access scopes and grant types. The token store is used to store 
the token. We will be using an in-memory token store.@EnableAuthorizationServer enables an Authorization Server (i.e. an AuthorizationEndpoint and 
a TokenEndpoint) in the current application context. Class AuthorizationServerConfigurerAdapter implements AuthorizationServerConfigurer which 
provides all the necessary methods to configure an Authorization server.

Above configuration

Registers a client with client-id �my-trusted-client� and password �secret� and roles & scope he is allowed for.
Specifies that any generated access token will be valid for only 120 seconds
Specifies that any generated refresh token will be valid for only 600 seconds

3. Security Configuration
Gluing everything together. Endpoint /oauth/token is used to request a token [access or refresh]. Resource owners [bill,bob] are configured here 
itself.


Spring Security is built on an ordered list of filter chains, and for each request the first one with a matching path handles the authentication. 
You have 3 filter chains in your combined app, one created by @EnableAuthorizationServer (with default order=0), one created by @EnableResourceServer 
(with default order=3)

----
RUN:
----
1. http://localhost:8082/010-Rest-OAuth/user/
	HTTP-METHOD: GET
	Accept: application/json
	
	RESULT: 401 Unauthorized

2. http://localhost:8082/010-Rest-OAuth/oauth/token?grant_type=password&username=admin&password=admin
	HTTP-METHOD: POST
	Accept: application/json
	Basic Auth: Username-my-trusted-client, Password-secret
	
	RESULT:
		{
		  "access_token": " 	",
		  "token_type": "bearer",
		  "refresh_token": "36c8ac25-a0c9-418c-bf4e-6b22a63e72e2",
		  "expires_in": 119,
		  "scope": "read write trust"
		}

3. http://localhost:8082/010-Rest-OAuth/user/?access_token=d9719fe7-348e-4ed9-b3bf-106551dc8149
	HTTP-METHOD: GET
	Accept: application/json
	
	RESULT: 200 OK
	
	RESULT AFTER ACCESS_TOKEN EXPIRED:
		{
		  "error": "invalid_token",
		  "error_description": "Access token expired: d9719fe7-348e-4ed9-b3bf-106551dc8149"
		}

4. http://localhost:8082/010-Rest-OAuth/oauth/token?grant_type=refresh_token&refresh_token=e4816e94-a352-4bbb-8f2e-00330a657029
	HTTP-METHOD: POST
	Accept: application/json
	Basic Auth: Username-my-trusted-client, Password-secret
	
	RESULT:
		{
		  "access_token": " 	",
		  "token_type": "bearer",
		  "refresh_token": "36c8ac25-a0c9-418c-bf4e-6b22a63e72e2",
		  "expires_in": 119,
		  "scope": "read write trust"
		}
		
	RESULT AFTER REFRESH_TOKEN EXPIRED:
		{
		  "error": "invalid_token",
		  "error_description": "Invalid refresh token (expired): 2baf0cfe-916e-4744-a2c3-028a3c3fda49"
		}
		
	At this point, you have to again generate access token and refresh token.		

 */
package _001;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class UserController {
	@Autowired
	UserDAO userDAO;

	@RequestMapping(value="/user", method=RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})  
	public ResponseEntity<List<User>> getAllUsers(){
		return new ResponseEntity<List<User>>(userDAO.getUsers(), HttpStatus.OK);
	}

	@RequestMapping(value="/user/{id}", method=RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})  
	public User viewEmp(@PathVariable("id") int id){
		return userDAO.getUserById(id);
	}
	
	@RequestMapping(value = "/user", method = RequestMethod.POST, consumes={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> createUser(@RequestBody User user,  UriComponentsBuilder ucBuilder) {
		userDAO.createUser(user); 
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/user/{id}").buildAndExpand(user.getId()).toUri());
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }
	
	@RequestMapping(value = "/user/{id}", method = RequestMethod.PUT, consumes={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<User> updateUser(@PathVariable("id") int id, @RequestBody User user) {
		userDAO.updateUser(user);
        return new ResponseEntity<User>(user, HttpStatus.OK);
    }
	
	@RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUser(@PathVariable("id") int id) {
		userDAO.deleteUser(id);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

}
