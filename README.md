# Blockchain-Voting

This is the blockchain based peer-to-peer voting application designed for SOFE4790U Distributed Systems (Fall 2020–Dr. Q. Mahmoud).

This application was designed for **Ubuntu 20.04 LTS with Java 11**, please ensure these requirements are met before running the application.

Steps to run:
1. Compile all the java files using javac *.java
2. Start **rmiregistry** in the project directory
3. Start a blockchain peer using the command **./BlockChain.sh node1** where noe1 is the name of the node. 
4. Add as many subsequent nodes using the command **./BlockChain.sh {new node name} {ip address of any existing node} {name of any existing node}**
5. Start the admin client with the command **./AdminClient.sh {IP address of any node on the netwrok} {name of any node on the network}**
6. Start the application client with the command **./Application.sh {IP address of any node on the work} {name of any node on the network}**