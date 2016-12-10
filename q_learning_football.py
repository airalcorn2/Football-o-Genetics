# Michael A. Alcorn

import csv
import random


def get_turnover_rates(turnover_f):
    """Reads in turnover data from a file. 
    
    :param turnover_f: 
    :return: 
    """
    fieldnames = ["player", "rate"]
    reader = csv.DictReader(open(turnover_f), fieldnames = fieldnames)
    turnovers = {}
    for row in reader:
        player = row["player"]
        rate = float(row["rate"])
        turnovers[player] = rate
    
    return turnovers


def get_stats(keep_players):
    """Reads in various data files.
    
    :return: 
    """
    interceptions = get_turnover_rates("InterceptionRates.csv")
    fumbles = get_turnover_rates("FumbleRates.csv")
    turnovers = {"Pass": interceptions, "Rush": fumbles}
    
    fieldnames = ["opponent", "quarter", "down", "distance", "side_of_field", "yard_line", "first_initial", "last_name", "play", "yards"]
    reader = csv.DictReader(open("Results/RawData.csv"), fieldnames = fieldnames)
    
    data = {}
    down_distance_data = {}
    
    for row in reader:
        
        # All stats.
        play = row["play"]
        if play not in data:
            data[play] = {}
        
        player = row["first_initial"] + " " + row["last_name"]
        if player not in keep_players:
            continue
        
        if player not in data[play]:
            data[play][player] = []
        
        yards = int(row["yards"])
        data[play][player] += [yards]
        
        # Down and distance stats.
        down = row["down"]
        distance = row["distance"]
        distance_cat = ""
        
        if down != "1st":
            if distance == "Goal":
                distance = row["yard_line"]
            
            distance = int(distance)
            if distance <= 2:
                distance_cat = "Short"
            elif 2 < distance <= 6:
                distance_cat = "Med"
            else:
                distance_cat = "Long"
        
        if down == "4th":
            down = "3rd"
        
        key = down + distance_cat + play
        
        if key not in down_distance_data:
            down_distance_data[key] = {}
        
        if player not in down_distance_data[key]:
            down_distance_data[key][player] = []
        
        down_distance_data[key][player] += [yards]
    
    return (data, down_distance_data, turnovers)


def get_down_str(down):
    """Converts an integer down to a string.
    
    :param down: 
    :return: 
    """
    if down == 1:
        return "1st"
    elif down == 2:
        return "2nd"
    elif down == 3:
        return "3rd"
    else:
        return ""


def get_distance_cat(down, distance):
    """Converts an integer distance to a string category.
    
    :param down: 
    :param distance: 
    :return: 
    """
    if down > 1:
        if distance <= 2:
            return "Short"
        elif 2 < distance <= 6:
            return "Med"
        else:
            return "Long"
    else:
        return ""


def get_epsilon_greedy_action(epsilon, Q_actions):
    """Choose an action using the epsilon-greedy strategy.
    
    :param epsilon: 
    :param Q_actions: 
    :return: 
    """
    if random.random() > epsilon:
        max_actions = []
        max_v = float("-inf")
        for action in Q_actions:
            if Q_actions[action] > max_v:
                max_v = Q_actions[action]
                max_actions = [action]
            elif Q_actions[action] == max_v:
                max_actions.append(action)
        
        return random.choice(max_actions)
    else:
        return random.choice(list(Q_actions.keys()))


def q_learning(data, turnovers):
    """Run Q-learning algorithm.
    
    :param data: 
    :param turnovers: 
    :param down_distance_player_a_prob: 
    :return: 
    """
    Q = {}
    for field in range(1, 100):
        for down in ["1st", "2nd", "3rd"]:
            for distance_cat in ["Short", "Med", "Long"]:
                state = "{0}-{1}-{2}".format(field, down, distance_cat)
                down_distance = down + distance_cat
                if down == "1st":
                    state = "{0}-{1}".format(field, down)
                    down_distance = down
                
                Q[state] = {}
                for play in ["Pass", "Rush"]:
                    for player in data[down_distance + play]:
                        action = play + "-" + player
                        Q[state][action] = 0
    
    alpha = 0.1
    epsilon = 0.1
    gamma = 0.9
    max_iters = 100000
    
    for iter in range(max_iters):
        
        down = 1
        distance = 10
        field = 27
        
        while down <= 3:
            
            down_str = get_down_str(down)
            distance_cat = get_distance_cat(down, distance)
            state = "{0}-{1}-{2}".format(field, down_str, distance_cat)
            if down_str == "1st":
                state = "{0}-{1}".format(field, down_str)
            
            action = get_epsilon_greedy_action(epsilon, Q[state])
            (play, player) = action.split("-")
            key = down_str + distance_cat + play
            
            # Check for turnover.
            if random.random() < turnovers[play].get(player, 0):
                Q[state][action] = Q[state][action] + alpha * (0 + gamma * 0 - Q[state][action])
                break
            
            yards = random.choice(data[key][player])
            field += yards
            # Touchdown!
            if field >= 100:
                Q[state][action] = Q[state][action] + alpha * (1 + gamma * 0 - Q[state][action])
                break
            
            distance -= yards
            down += 1
            
            if distance <= 0:
                down = 1
                if field > 90:
                    distance = 10 - field
                else:
                    distance = 10
            
            # Punt.
            if down == 4:
                Q[state][action] = Q[state][action] + alpha * (0 + gamma * 0 - Q[state][action])
                break
            
            down_str = get_down_str(down)
            distance_cat = get_distance_cat(down, distance)
            next_state = "{0}-{1}-{2}".format(field, down_str, distance_cat)
            if down_str == "1st":
                next_state = "{0}-{1}".format(field, down_str)
            
            Q[state][action] = Q[state][action] + alpha * (0 + gamma * max(Q[next_state].values()) - Q[state][action])
    
    return Q


def main():
    keep_players = set(["C Newton", "D Williams", "M Tolbert", "J Stewart"])
    (data, down_distance_data, turnovers) = get_stats(keep_players)
    Q = q_learning(down_distance_data, turnovers)

if __name__ == "__main__":
    main()