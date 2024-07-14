# stackoverflow-competitor

Every developer has a tab open to Stack Overflow. For over 15 years we’ve been the Q&A platform of choice that millions of people visit monthly to ask questions, learn, and share technical knowledge.

## About the Website
StackOverflowCompetitor is a web application designed to mimic the functionality of Stack Overflow, a popular platform for asking and answering technical questions. It features user authentication, question posting, answer submissions with media uploads, voting on content, tag-based searching, and browsing. The application leverages Spring Boot and Java technologies for backend development, ensuring robust and scalable performance for its users.

### It contains the following features
1. Top questions to be shown on the home page
2. Users can create a profile (Register, Login, Logout)
3. Users can post a question, tag a question
4. Users can answer to a question.
5. Rich media content (photos/videos) can be added as an answer.
6. Users can answer to an answer.
7. Users can vote for an answer or question
8. Users can search the tags and browse the questions by tags
9. Users can search questions/answers by text

### Assumptions
1. Tags should be managed by admin, and only those tags will be added that are present in DB.
2. User can only add tags while posting a question.
3. Focused mainly on creating logic, we can achieve other features with minimal adjustment.
4. Used local storage for media storage, we can use S3 for scalable systems.
5. Used sql with query based text searching, we can use elastic search for complex texts and keywords.
6. User can reply/answer to a question as many time as he wants.
7. User can vote on question/answer/reply
   
### Tools and technologies used
1. Java
2. Spring Boot
3. Spring Security
4. Junit and Mockito
5. Mysql
6. Postman
7. Intellij

## Plan of Action 
- Identify the Entities and Relationships

- List down the required behaviors or operations that need to be performed on these entities

- List down various endpoints required

- Start With user creation, authentication, and related stuff.

- Start with adding a question for the authenticated user.

- Adding a question with a pre-defined Tag associated with it.

- Adding an answer to a question

- Adding to reply to a particular answer to a question

- Voting on a question or an answer

- Searching functionalities based on top-rated, tags and text provided.

- Unit Test cases alongside development

## Security 

Security is one of the key features for any application to stay protected. In my application I have implemented **user authentication**, wherein the user has to register first and then log in to access the protected routes. To ensure proper password protection, I have used **Bcrypt and hashed the password**, to ensure good protection.
**Implemented private route** so that unauthorized users (not registered or logged in) can’t access the router with DB manipulations like AddQuestion, Addanswer, Vote, etc.

### ER Diagram

<a href="https://ibb.co/5rSNHmd"><img src="https://i.ibb.co/n7YHJ9T/stackoverflowcompetitor-ER-diagram.png" alt="stackoverflowcompetitor-ER-diagram" border="0"></a>

#### Api Designs

1. **POST /auth/register**
   - Register new user.

2. **POST /auth/login**
   - User login authentication.

3. **POST /auth/logout**
   - Logout current user session.
     
4. **POST /questions/postQuestion**
   - Post a question.
     
5. **POST /answers/question/{questionId}**
   - Post a answer to a question.

6. **POST /answers/reply/{questionID}/{answerId}**
   - Post a reply to an answer.
  
7. **POST /votes/question/{questionId}/{isUpvote}**
   - Vote a question.
     
8. **POST /votes/answer/{answerId}/{isUpvote}**
   - Vote an answer.

9. **GET /questions/top-voted**
   - Retrieve top-voted questions.

10. **GET /questions/getAllQuestions**
   - Get all questions.

11. **GET /questions/by-tag**
   - Filter questions by tag.

12. **GET /questions/search**
   - Filter questions by text.

13. **GET /answers/search**
   - Filter answers by text.
     
### Sequence Diagrams

#### User Flow
<a href="https://ibb.co/KWzFgXs"><img src="https://i.ibb.co/qpFk8dg/User-Sequence-Diagram.png" alt="User-Sequence-Diagram" border="0"></a>

#### Answer Flow
<a href="https://ibb.co/9r6QRnN"><img src="https://i.ibb.co/1m1FHZL/Answer-Squence-Diagram.png" alt="Answer-Sequence-Diagram" border="0"></a>

#### Question Flow
<a href="https://ibb.co/9grRbDN"><img src="https://i.ibb.co/nmLh7Gb/Question-sequence-diagram.png" alt="Question-sequence-diagram" border="0"></a>

#### Vote Flow
<a href="https://ibb.co/Lh4sxpw"><img src="https://i.ibb.co/yBLx6sT/Vote-sequence-diagram.png" alt="Vote-sequence-diagram" border="0"></a>

### Expected Outcomes:
1. State the assumptions ✅
2. High-level design of components/microservices and interaction
   between components ✅
3. Database schema design ✅
4. Technology stack and data store choices ✅
5. Critical APIs - Select the top 3 most trafficked APIs and explain the
   design and implementation

https://docs.google.com/document/d/13PgflspeNH8IbGCvzYUfyVy38nk3oUg_87aQiHWD4Pg/edit
