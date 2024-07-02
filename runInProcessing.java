//clean.txt file source (vulgarities/profanity has been parsed out, and the list has been modified):
//https://github.com/dwyl/english-words/issues/63 > https://github.com/dwyl/english-words/files/3086945/clean_words_alpha.txt

//variables
//global
int currentScreen = 0;
int attempt = 0;
int currentBox = 0;
boolean guess = false;
boolean result;
String word;
int r;
String[] words;

char[][] wordAttempt;
char[][] boxState;
char[] possibleChar;

PFont menuFont, gameFont, endFont, endFont2, endFont3;

//menu
float[] menuButtonX = new float [2];
float[] menuButtonY = new float [2];
float[] menuButtonXd = new float [2];
float[] menuButtonYd = new float [2];

//container array for displaying each row from a class object
row[] row = new row[5];

//setup
void setup()
{
  size (1400, 800);

  rectMode(CENTER);
  textAlign(CENTER);

  //fonts
  menuFont = createFont("CourierNewPS-BoldMT", 84);
  gameFont = createFont("Helvetica-Bold", 84);
  endFont = createFont("Helvetica-Bold", 63);
  endFont2 = createFont("Helvetica-Bold", 42);
  endFont3 = createFont("Helvetica-Bold", 21);

  //start button
  menuButtonX[0] = width/2;
  menuButtonY[0] = height/2;
  menuButtonXd[0] = 400;
  menuButtonYd[0] = 100;
  //restart button
  menuButtonX[1] = width/2;
  menuButtonY[1] = height/2 + 50;
  menuButtonXd[1] = 300;
  menuButtonYd[1] = 100;

  background(255);
  words = loadStrings("clean.txt"); //reads each line in the clean.txt file containng all the words

  reset(); //calls the reset() function to generate a new word and reset the game
}

//main menu
void draw()
{
  //currentScreen determines the game menu the program prints to the window (0 is start, 1 is the actual game, 2 is the end screen)
  if (currentScreen == 0)
  {
    startMenu(currentScreen);
  }
  if (currentScreen == 1)
  {
    gameMenu(currentScreen);
  }
  if (currentScreen == 2)
  {
    endMenu(currentScreen);
  }
}


int startMenu(int currentScreen)
{
  background(255);
  textFont(menuFont);
  fill(0);
  text("Wordle+", width/2, height/4);

  //small touch to make the button look nicer when hovered over (dark when hovering, light when not)
  if (mouseX <= menuButtonX[0]+(menuButtonXd[0])/2 && mouseX >= menuButtonX[0]-(menuButtonXd[0])/2 &&
    mouseY <= menuButtonY[0]+menuButtonYd[0]/2 && mouseY >= menuButtonY[0]-menuButtonYd[0]/2) {
    fill(157, 191, 146);
  } else {
    fill(172, 206, 161);
  }
  rect(menuButtonX[0], menuButtonY[0], menuButtonXd[0], menuButtonYd[0], 20);

  //title and short instructions, menu stuff
  fill(0);
  text("Start", menuButtonX[0], menuButtonY[0] + 25);
  textFont(endFont2);
  text("You will get 5 chances to guess a word between 4 to 7 letters.", width/2, 3*height/4);
  text("Good Luck!", width/2, 3*height/4 + 65);

  return currentScreen;
}

int gameMenu(int currentScreen)
{
  int h = height/7;
  background(255);
  textFont(gameFont);
  strokeWeight(2);


  if (attempt < 5 && guess == false) //if less than 5 attempts and guess == false (incorrect), keep playing
  {
    for (int j = 0; j < 5; j++) //displays the 5 rows of the wordle game, calls the row class object and display function within the class
    {
      row[j] = new row(j, h);
      row[j].display();
      h = h + 145;
    }
  } else if (attempt == 5 && guess == false) //switch the program to disply the end screen
  {
    result = false;
    currentScreen = 2;
  }
  return currentScreen;
}


int endMenu(int currentScreen) //end screen
{
  int h = height/7;
  for (int j = 0; j < 5; j++)
  {
    row[j] = new row(j, h);
    row[j].display();
    h = h + 145;
  }
  if (result == true)
  {
    winScreen();
  } else if (result == false)
  {
    loseScreen();
    println(result);
    println(attempt);
  }
  return currentScreen;
}

void keyPressed()
{
  if (currentScreen == 1 && attempt < 5 && guess == false)
  {
    //takes the character entered, limiting the number that can be entered by the size of the array's row-length
    if ((key >= 'A' && key <= 'Z') || (key >= 'a' && key <= 'z') && currentBox < word.length())
    {
      wordAttempt[currentBox][attempt] = toUpper(key);
      currentBox++;
    } else if (keyCode == BACKSPACE && wordAttempt[0][attempt] != '\0') //erase last charcter entered
    {
      wordAttempt[currentBox-1][attempt] = '\0';
      currentBox--;
    } else if (keyCode == ENTER && wordAttempt[word.length()-1][attempt] != '\0') //determines if characters are correct, exist in a separate place, or wrong
    {
      for (int i = 0; i < word.length(); i++)
      {
        boxState[i][attempt] = 'n';
        if (wordAttempt[i][attempt] == word.charAt(i))
        {
          boxState[i][attempt] = 'g';
        } else
        {
          for (int j = 0; j < word.length(); j++)
          {
            if (wordAttempt[i][attempt] == possibleChar[j])
            {
              boxState[i][attempt] = 'y';
            } else
            {
              boxState[i][attempt] = boxState[i][attempt];
            }
          }
        }
      }
      guess = answerCheck(boxState); //calls answerCheck function to determine if the guess is correct
      if (guess == false && attempt == 4) //if not correct and no more attempts left, end game as result = wrong
      {
        currentScreen = 2;
        result = false;
      } else if (guess == false)
      {
        attempt++;
        currentBox = 0;
      } else if (guess == true)
      {
        currentScreen = 2;
        result = true;
      }
    }
  }
}



void mousePressed()
{
  //determines if the cursor position is within the x-values for certain buttons when the mouse is pressed
  //changes screen from menu to the game
  if (mouseX <= menuButtonX[0]+(menuButtonXd[0])/2 && mouseX >= menuButtonX[0]-(menuButtonXd[0])/2 &&
    mouseY <= menuButtonY[0]+menuButtonYd[0]/2 && mouseY >= menuButtonY[0]-menuButtonYd[0]/2 && currentScreen == 0)
  {
    if (currentScreen == 0)
    {
      currentScreen = 1;
    }
  }

  //changes screen from end menu back to the game, calls reset() function to regenerate a new word
  if (mouseX <= menuButtonX[1]+(menuButtonXd[1])/2 && mouseX >= menuButtonX[1]-(menuButtonXd[1])/2 &&
    mouseY <= menuButtonY[1]+menuButtonYd[1]/2 && mouseY >= menuButtonY[1]-menuButtonYd[1]/2 && currentScreen == 2)
  {
    reset();
  }
}

boolean answerCheck(char[][] boxState) //checks of the last entered guess is correct or not
{
  boolean x = true; //if false after looping, word is incorrect, if true after looping, word is correct (game over)
  for (int i = 0; i < word.length(); i++)
  {
    if (boxState[i][attempt] == 'g')
    {
      x = x;
    } else if (boxState[i][attempt] == 'y' || boxState[i][attempt] == 'n')
    {
      x = false;
    }
  }
  return x;
}

char toUpper(char input) //changes all entered characters to upper case so that ASCII values may be compared when "enter" is clicked
{
  char upper = '\0';
  if (input <= 'z' && input >= 'a')
  {
    upper = char(input - 32);
  } else if (input <= 'Z' && input >= 'A')
  {
    upper = input;
  }
  return upper;
}



class row //draws the rows during the game- organizational purposes
{
  int j;
  int h;
  int counter = 1;
  int l = word.length();
  int w = ((width - (l*100 + (l-1)*45)) / 2) + 50;

  row(int tempJ, int tempH)
  {
    j = tempJ;
    h = tempH;
  }

  void display()
  {
    rectMode(CENTER);
    textAlign(CENTER, CENTER);
    for (int i = 0; i < word.length(); i++)
    {
      if (boxState[i][j] == '\0')
      {
        fill(255, 255, 255);
        rect(w, h, 100, 100);
        fill(0);
        textFont(gameFont);
        text(wordAttempt[i][j], w, h);
      } else if (boxState[i][j] == 'g')
      {
        fill(108, 169, 101);
        rect(w, h, 100, 100);
        fill(255);
        textFont(gameFont);
        text(wordAttempt[i][j], w, h);
      } else if (boxState[i][j] == 'y')
      {
        fill(200, 182, 83);
        rect(w, h, 100, 100);
        fill(255);
        textFont(gameFont);
        text(wordAttempt[i][j], w, h);
      } else if (boxState[i][j] == 'n')
      {
        fill(120, 124, 127);
        rect(w, h, 100, 100);
        fill(255);
        textFont(gameFont);
        text(wordAttempt[i][j], w, h);
      }

      if (counter % word.length() == 0)
      {
        w = width/word.length();
      } else
      {
        w = w + 145;
      }
      counter++;
    }
  }
}

void reset() //regenerates a new word, makes sure the intial conditions for the game to run are reset from the end screen
{
  if (currentScreen == 0)
  {
    r = int(random(words.length));
    word = words[r].toUpperCase();
    while (word.length() < 4 || word.length() > 7)
    {
      r = int(random(words.length));
      word = words[r].toUpperCase();
    }
    println(word);
    wordAttempt = new char[word.length()][5];
    boxState = new char[word.length()][5];
    possibleChar = new char[word.length()];

    for (int i = 0; i < word.length(); i++)
    {
      possibleChar[i] = word.charAt(i);
    }
  } else if (currentScreen == 2)
  {
    r = int(random(words.length));
    word = words[r].toUpperCase();
    while (word.length() < 4 || word.length() > 7)
    {
      r = int(random(words.length));
      word = words[r].toUpperCase();
    }
    println(word);
    wordAttempt = new char[word.length()][5];
    boxState = new char[word.length()][5];
    possibleChar = new char[word.length()];

    for (int i = 0; i < word.length(); i++)
    {
      possibleChar[i] = word.charAt(i);
    }
    currentScreen = 1;
    currentBox = 0;
    attempt = 0;
    guess = false;
  }
}

void winScreen() //player wins
{
  fill(220, 120);
  rect(width/2, height/2, width + 10, height + 10);
  fill(255);
  rect(width/2, height/2, 440, 600, 10);
  fill(0);
  textFont(endFont);
  text("You Win!", width/2, height/2 - 225);
  textFont(endFont2);
  text("Attempts: " +(attempt+1), width/2, height/2 - 150);
  textFont(endFont3);
  text("(Don't share the answer!)", width/2, height/2 + 225);
  if (mouseX <= menuButtonX[1]+(menuButtonXd[1])/2 && mouseX >= menuButtonX[1]-(menuButtonXd[1])/2 &&
    mouseY <= menuButtonY[1]+menuButtonYd[1]/2 && mouseY >= menuButtonY[1]-menuButtonYd[1]/2)
  {
    fill(157, 191, 146);
  } else
  {
    fill(172, 206, 161);
  }
  rect(width/2, height/2 + 50, 300, 100, 10);
  textFont(endFont2);
  fill(255);
  text("New Word", width/2, height/2 + 45);
}

void loseScreen() //player loses
{
  fill(220, 120);
  rect(width/2, height/2, width + 10, height + 10);
  fill(255);
  rect(width/2, height/2, 440, 600, 10);
  fill(0);
  textFont(endFont);
  text("You Lose!", width/2, height/2 - 225);
  textFont(endFont2);
  text("The word was:", width/2, height/2 - 150);
  text(word, width/2, height/2 - 100);
  textFont(endFont3);
  text("(Don't share the answer!)", width/2, height/2 + 225);
  if (mouseX <= menuButtonX[1]+(menuButtonXd[1])/2 && mouseX >= menuButtonX[1]-(menuButtonXd[1])/2 &&
    mouseY <= menuButtonY[1]+menuButtonYd[1]/2 && mouseY >= menuButtonY[1]-menuButtonYd[1]/2)
  {
    fill(157, 191, 146);
  } else
  {
    fill(172, 206, 161);
  }
  rect(width/2, height/2 + 50, 300, 100, 10);
  textFont(endFont2);
  fill(255);
  text("New Word", width/2, height/2 + 45);
}
