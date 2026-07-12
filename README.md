# Decentralized-Medical-Records-Management
Blockchain Based Medical Records Management System




## Testing

Basic unit tests are included for the Authentication and Blockchain modules.

### How to Run Tests
1. Compile the test files along with the main source files:

2. Run the test files:
3. ### Test Coverage
- Authentication: login(), logout()
- Blockchain: block creation, chain integrity validation

## Project Modules
- **Authentication.java** – Handles user login/logout
- **Blockchain.java** – Core blockchain logic (hashing, chain validation)
- **Patient.java / Doctor.java / MedicalRecord.java** – Database entity classes
- **IPFSService.java** – IPFS integration skeleton for decentralized file storage
- **login.html / dashboard.html / style.css** – Frontend UI pages

## Team Contribution
| Member | Module |
|--------|--------|
| Omkar | Blockchain Core Logic |
| Prasad | Database + Entities + IPFS |
| Naresh | Frontend/UI |
| Rohan | Testing + Documentation |

## How to Run

### Blockchain Module
javac Blockchain.java
java Blockchain

### Authentication Module (with tests)
javac AuthenticationTest.java Authentication.java
java AuthenticationTest

### Frontend
Open login.html or dashboard.html in any web browser