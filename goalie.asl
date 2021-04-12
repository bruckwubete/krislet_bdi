// Agent striker in project krislet_player

/* Initial beliefs and rules */
!join_the_game.

+!join_the_game <- join_team(X); init_x(X); init_y(Y); station(S, D); !spwan.
+goal_l <- .drop_all_desires; !spwan.
+goal_r <- .drop_all_desires; !spwan.
+goal_kick_r <- .drop_all_desires; !spwan.
+goal_kick_l <- .drop_all_desires; !spwan.

+!spwan <- .wait(team(T) & (T == left | T == right));
           .wait(1000);
           .wait(init_x(X));
           .wait(init_y(Y));
           move(X, Y);
           !defend.

+!defend : team(T) <-  !find_ball;
                       !track_ball;
                       if(T == left) {
                          .wait(kick_off_l | play_on);
                       } elif(T == right) {
                         .wait(kick_off_r | play_on);
                       } else {
                          .print("team not found");
                       }
                       !dash_towards_ball;
                       !catch_ball;
                       if(T == left) {
                         !dash_to("flag", "plc");
                       } elif(T == right) {
                         !dash_to("flag", "prc");
                       }
                       if(T == left) {
                         !strike_to_goal("r");
                       } elif(T == right) {
                         !strike_to_goal("l");
                       }
                       !return_to_station;
                       !defend.

+!return_to_station : station(S, D) <- .printf("trying to find %s %s", S, D); !find(S, D); !dash_to(S, D).

+!find(S, D) : not(distance(S, D, X)) <- .printf("turning to find %s %s", S, D); turn(50); !find(S, D).
+!find(S, D) : true <- .printf("found station %s %s", S, D).

+!dash_to(S, D) : direction(S, D, X) & (X > 2.0 | X < -2.0) <- turn(X); !dash_to(S, D).
+!dash_to(S, D)
    : (distance(S, D, X) & X > 3.0) & (direction(S, D, Y) & (Y >= -2.0 & Y <= 2.0))
        <-  .suspend(find(S, D));
            dash(X); !dash_to(S, D).
+!dash_to(S, D) : not(distance(S, D, X)) <- !find(S, D); !dash_to(S, D).
+!dash_to(S, D) : true <- .printf("done dashing to station %s %s",S,D).



+!find_ball : not(distance("ball", "", X)) <- turn(100); !find_ball.
+!find_ball : direction("ball", "", X) & (X > 10.0 | X < -10.0) <- turn(X); !find_ball.
+!find_ball : true <- .print("done, found ball!"); !track_ball.

+!track_ball : not(direction("ball", _, _)) <- !find_ball.
+!track_ball : direction("ball", "", X) & distance("ball", "", Y) & Y > 15.0 <- turn(X); !track_ball. //suspend based on distance
+!track_ball : true <- .print("done, ball in range!").

+!dash_towards_ball : direction("ball", "", X) & (X > 2.0 | X < -2.0) <- turn(X); !dash_towards_ball.
+!dash_towards_ball
    : (distance("ball", "", X) & X > 1.0) & (direction("ball", "", Y) & (Y >= -2.0 & Y <= 2.0))
        <-  .suspend(find_ball);
            if(X > 15) {
                !return_to_station;
            } else {
                dash(X); !dash_towards_ball;
            }.
+!dash_towards_ball : true <- .print("done, reached ball!").

+!catch_ball : not(distance("ball", "", X)) <- !return_to_station.
+!catch_ball : distance("ball", "", X) & direction("ball", "", Y) <- catch(Y).
+!catch_ball : true <- .print("done, trying to catch the ball!").

+!strike_to_goal(G) : distance("goal", G, X) & direction("goal", G, Y) <- kick(10, Y); .printf("done, kicked to goal %s", G).
+!strike_to_goal(G) : not(distance("goal", G, X)) <- !find("goal", G); !strike_to_goal(G); .printf("done, kicked to goal %s", G).