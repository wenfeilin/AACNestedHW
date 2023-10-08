import structures.AssociativeArray;
import structures.KeyNotFoundException;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.PrintWriter;

/**
 * Represents all the mappings for a home page of items that should be displayed
 * 
 * @author Wenfei Lin
 * @author Catie Baker
 */
public class AACMappings {

  // Fields

  // home page category
  AACCategory categoryImgsToCategoryNames;
  // categories' items (images and names)
  AssociativeArray<String, AACCategory> categoryNamesToCategoryItems; 
  String currentCategory;


  // Constructor

  /**
   * Reads in the file and creates the relevant mappings from images to categories 
   * and adds all the items to each category. Should also start the AAC on the home screen
   * @param filename
   */
  public AACMappings(String filename) { // would have to do error checking for this method LATER
    // has to parse file for info
    PrintWriter pen = new PrintWriter(System.out, true);
    try {
      BufferedReader reader = new BufferedReader(new FileReader(filename));
      this.categoryImgsToCategoryNames = new AACCategory(""); // "" for home page
      this.categoryNamesToCategoryItems = new AssociativeArray<String,AACCategory>();
      String line;
      String currentReadCategory = ""; // for later when adding items to the category

      // keep looping while the end of file hasn't been reached
      while((line = reader.readLine()) != null) { 
        String[] words = line.split(" ");

        if (line.charAt(0) != '>') { // this line has category img and category name
          String categoryImgLoc = words[0];
          String categoryName = words[1];

          currentReadCategory = categoryName;
          // Adds the category's image and its name to the array of all categories (home page)
          this.categoryImgsToCategoryNames.addItem(categoryImgLoc, categoryName);

          // create new category
          AACCategory categoryItems = new AACCategory(currentReadCategory);
          // add new category to array of all categories
          this.categoryNamesToCategoryItems.set(currentReadCategory, categoryItems);
        } else { // this line has item img and item name
          String itemImgLoc = words[0].substring(1);
          String itemName = "";

          // In case the item name is multiple words
          for (int i = 1; i < words.length; i++) {
            itemName += words[i] + " ";
          }
          itemName = itemName.trim();

          try {
            // Adds the item's image and name to the specified category in the array detailing 
            // all categories' items
            AACCategory categoryArr = this.categoryNamesToCategoryItems.get(currentReadCategory);
            categoryArr.addItem(itemImgLoc, itemName);
          } catch (Exception e) {
            pen.println("IDK what i'll do here"); // CHANGE LATER
          }
        }
      }
      reader.close(); // closes reader
    } catch (FileNotFoundException e) {
      pen.println("File was not found."); // CHANGE LATER
      e.printStackTrace(); // CHANGE LATER
    } catch (IOException e) {
      // do sth
      pen.println("I/O Exception"); // CHANGE LATER
    }

    this.currentCategory = ""; // on home page
  } // AACMappings()


  // Methods

  // REMOVE LATER OR MAKE IT INTO A TOSTRING OF SOME SORT 
  public void print() {
    // prints all category names
    int size = this.categoryImgsToCategoryNames.getImages().length;
    for (int i = 0; i < size; i++) {
      try {
        System.out.println(this.categoryImgsToCategoryNames.getText(this.categoryImgsToCategoryNames.getImages()[i]));
      } catch (KeyNotFoundException e) {
        e.printStackTrace();
      }
    }

    // prints all items in each category
    String toBePrinted = this.categoryNamesToCategoryItems.toString();
    System.out.println(toBePrinted);
    
  }

  /**
   * Given the image location selected, it determines the associated text with the image. 
   * If the image provided is a category, it also updates the AAC's current category to 
   * be the category associated with that image
   * 
   * @param imageLoc the location where the image is stored
   * @return returns the text associated with the current image
   */
  public String getText(String imageLoc) throws ElementNotFoundException {
    return "television";  // STUB
  } // getText(String)
  
  /**
   * Provides an array of all the images in the current category
   * 
   * @return the array of images in the current category
   */
  public String[] getImageLocs() {
    return new String[] { "img/food/icons8-french-fries-96.png", "img/food/icons8-watermelon-96.png" }; // STUB
  } // getImageLocs()

  /**
   * Resets the current category of the AAC back to the default category
   */
  public void reset() {
    this.currentCategory = "";
  } // reset()

  /**
   * Gets the current category
   * @return returns the current category or the empty string if on the default category
   */
  public String getCurrentCategory() {
    return this.currentCategory; 
  } // getCurrentCategory()

  /**
   * Determines if the image represents a category or text to speak
   * 
   * @param imageLoc the location where the image is stored
   * @return true if the image represents a category, false if the image represents text to speak
   */
  public boolean isCategory(String imageLoc) {
    return false; // STUB
  } // isCategory(String)

  /**
   * Writes the ACC mappings stored to a file. The file is formatted as the text location of the 
   * category followed by the text name of the category and then one line per item in the category 
   * that starts with > and then has the file name and text of that image for instance: 
   * img/food/plate.png food 
   * >img/food/icons8-french-fries-96.png french fries 
   * >img/food/icons8-watermelon-96.png watermelon 
   * img/clothing/hanger.png clothing 
   * >img/clothing/collaredshirt.png collared shirt 
   * represents the file with two categories, food and clothing and food has french fries and 
   * watermelon and clothing has a collared shirt
   * @param filename the name of the file to write the AAC mapping to
   */
  public void writeToFile(String filename) {
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
      writer.write("can write whatever here!"); // alter later
      // OR
      for (String element : arrayName) {
        writer.write("sth\n");
      }
      // closes writer
      writer.close(); 
    } catch (IOException e) {
      // might change instead of printing stack trace or leave idk
      e.printStackTrace();
    }
    // unfinished STUB
  } // writeToFile(String)

  /**
   * Adds the mapping to the current category (or the default category if that is 
   * the current category)
   * 
   * @param imageLoc the location of the image
   * @param text the text associated with the image
   */
  public void add(String imageLoc, String text) {
    // STUB
  } // add(String, String)
} // class AACMappings
