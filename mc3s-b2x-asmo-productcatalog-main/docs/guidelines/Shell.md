# Shell Commands

B2x provides Shell commands
using [Spring Shell](Khttps://docs.spring.io/spring-shell/reference/getting-started.html)

The shell is enabled per default when starting in spring profile local

```
Standard Commons Logging discovery in action with spring-jcl: please remove commons-logging.jar from classpath in order to avoid potential conflicts


                                                 $$$$$$$$                   
                                                $$$$$$$$$$$                 
                                                $$$$$$$$$$$                 
                                                $$    $$$$$$                
            $$$$  $$$$    $$$$        $$$$$$$          $$$$$      $$$$$$$   
            $$$$$$$$$$$ $$$$$$$     $$$$$$$$$          $$$$$    $$$$$$$$$$  
            $$$$$$$$$$$$$$$$$$$$   $$$$$$$$$$        $$$$$$     $$$$$$$$$$  
            $$$$$$$$$$$$$$$$$$$$  $$$$$$$$$$$    $$$$$$$$$$    $$$$$$$$$$$  
            $$$$$$ $$$$$$$ $$$$$  $$$$$$         $$$$$$$$      $$$$$        
            $$$$$  $$$$$$  $$$$$  $$$$$          $$$$$$$$$$    $$$$$$$$     
            $$$$   $$$$$   $$$$$ $$$$$              $$$$$$$$    $$$$$$$$$   
            $$$$   $$$$$   $$$$$ $$$$$                $$$$$$    $$$$$$$$$$  
            $$$$   $$$$$   $$$$$ $$$$$$                $$$$$      $$$$$$$$  
            $$$$   $$$$$   $$$$$  $$$$$                $$$$$        $$$$$$$ 
            $$$$   $$$$$   $$$$$  $$$$$$               $$$$$   $$    $$$$$$ 
            $$$$   $$$$$   $$$$$  $$$$$$$$$$$   $$$$$$$$$$$$   $$$$$$$$$$$  
            $$$$   $$$$$   $$$$$   $$$$$$$$$$   $$$$$$$$$$$    $$$$$$$$$$$  
            $$$$   $$$$$   $$$$$      $$$$$      $$$$$$$$         $$$$$
 
            MACH Cloud Commerce solution - B2Xaccelerator

            Spring Boot  (v3.5.5) :: mc3s-b2x-integration


2025-08-28 11:31:42.424  INFO com.mindcurv.b2x.Mc3sRunnerApp [] Starting Mc3sRunnerApp v5.8.6113 using Java 17.0.13 with PID 22481 (/Users/dirk.paschke/.m2/repository/com/mindcurv/b2x/commons/mc3s-b2x-base/5.8.6113/mc3s-b2x-base-5.8.6113.jar started by dirk.paschke in /Users/dirk.paschke/Documents/b2x/mc3s-b2x-all-idea-project)
...
2025-08-28 11:31:53.067  INFO com.mindcurv.b2x.Mc3sRunnerApp [] Started Mc3sRunnerApp in 11.165 seconds (process running for 11.654)
shell:>
```

The available commands can be listed using help

```shell
shell:>help
AVAILABLE COMMANDS

B2x Shell Commands
       hello-world: 

Built-In Commands
       help: Display help about available commands
       stacktrace: Display the full stacktrace of the last error.
       clear: Clear the shell screen.
       quit, exit: Exit the shell.
       history: Display or save the history of previously run commands
       version: Show version info
       script: Read and execute commands from a file.

Guided Trial Cleanup Shell Commands
       guidedtrial-cleanupAll: 

Guided Trial Mail Shell Commands
       guidedtrial-mailById: 
       guidedtrial-purgeMails: 
       guidedtrial-mails: 

Import Container Shell Commands
       importcontainer-delete: 
       importcontainer-deleteAll: 
       importcontainer-list: 
```

The command can be executed like

```shell

shell:>hello-world "argument1"
Hello world argument1
```

