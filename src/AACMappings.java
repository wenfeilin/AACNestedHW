import structures.AssociativeArray;
import structures.KeyNotFoundException;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.File;

/**
 * Represents all the mappings for a home page of items that should be displayed
 * 
 * @author Wenfei Lin
 * @author Catie Baker
 */
public class AACMappings {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  // Home page (categories' images and names)
  AACCategory categoryImgsToCategoryNames;
  // Categories' items (images and text)
  AssociativeArray<String, AACCategory> categoryNamesToCategoryItems; 
  String currentCategory;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Reads in the file and creates the relevant mappings from images to categories 
   * and adds all the items to each category. Should also start the AAC on the home screen
   * @param filename
   */
  public AACMappings(String filename) { // ERROR CHECKING FORMAT OF FILE............?
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

          // Adds the item's image and name to the specified category in the array detailing 
          // all categories' items
          AACCategory categoryArr = this.getExceptionCatching(currentReadCategory);
          categoryArr.addItem(itemImgLoc, itemName);
        }
      }
      reader.close(); // closes reader
    } catch (FileNotFoundException e) {// if the file wasn't found
      pen.printf("Error: The file %s was not found.\n", filename);
    } catch (IOException e) { // some other input/output error
      pen.println(e.getMessage());
    }
    this.currentCategory = ""; // on home page
  } // AACMappings()

  // +----------------+----------------------------------------------
  // | Public Methods |
  // +----------------+

  /**
   * Given the image location selected, it determines the associated text with the image. 
   * If the image provided is a category, it also updates the AAC's current category to 
   * be the category associated with that image
   * 
   * @param imageLoc the location where the image is stored
   * @return returns the text associated with the current image
   */
  public String getText(String imageLoc) {
    String text = "Error: " + imageLoc + "does not have an associated text";

    // If the image represents a category
    if (categoryImgsToCategoryNames.hasImage(imageLoc)) {
      String categoryName = categoryImgsToCategoryNames.getText(imageLoc);
      this.currentCategory = categoryName;
      text = categoryName;
    } else {
      // Otherwise, the image represents an item (in a category)
      AACCategory categoryAAC = this.getExceptionCatching(this.currentCategory);

      if (categoryAAC.hasImage(imageLoc)) {
        text = categoryAAC.getText(imageLoc);
      }
    }
    return text;
  } // getText(String)
  
  /**
   * Provides an array of all the images in the current category
   * 
   * @return the array of images in the current category
   */
  public String[] getImageLocs() { 
    if (this.currentCategory.equals("")) { // if the current category is the home page
      return this.categoryImgsToCategoryNames.getImages();
    } else { // if the current category is not the home page
      return this.getExceptionCatching(this.currentCategory).getImages();
    }
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
    boolean result = false;

    if (this.categoryImgsToCategoryNames.hasImage(imageLoc)) { // If the image is a category image
      result = true; // Image represents a category
    } else {
      // assumed to return false but need to check if imageLoc even exists as an item image
      String[] categories = this.categoryNamesToCategoryItems.getKeysForAAC();
      boolean existsAsItem = false;

      for (String category : categories) {
        if (this.getExceptionCatching(category).hasImage(imageLoc)) {
          existsAsItem = true;
        }
      }

      if (!existsAsItem) {
        PrintWriter pen = new PrintWriter(System.out, true);
        pen.printf("%s is not an image representing a text to speak nor" + 
                   "is it an image representing a category.\n", imageLoc);
      }
    }
    return result;
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
    PrintWriter pen = new PrintWriter(System.out, true);

    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
      AACCategory categories = this.categoryImgsToCategoryNames;
      String[] categoryImgs = categories.getImages();

      for (String categoryImg : categoryImgs) {
        String categoryName = categories.getText(categoryImg);
        writer.write(categoryImg + " " + categoryName + "\n");

        AACCategory itemsInCategory = this.getExceptionCatching(categoryName);
        String[] itemImages = itemsInCategory.getImages();

        for (String itemImage : itemImages) {
          String itemName = itemsInCategory.getText(itemImage);
          writer.write(">" + itemImage + " " + itemName + "\n");
        }
      }
      // closes writer
      writer.close(); 
    } catch (IOException e) {
      pen.println(e.getMessage());
    }
  } // writeToFile(String)

  /**
   * Adds the mapping to the current category (or the default category if that is 
   * the current category)
   * 
   * @param imageLoc the location of the image
   * @param text the text associated with the image
   */
  public void add(String imageLoc, String text) {
    File imgFile = new File(imageLoc);
    boolean sameAsCategoryImg = this.categoryImgsToCategoryNames.hasImage(imageLoc);
    boolean sameAsCategoryItemsImg = false;
    PrintWriter pen = new PrintWriter(System.out, true);
    String[] itemImageLocs = this.getImageLocs();

    if (imageLoc.contains("\\")) {
      imageLoc = imageLoc.replace("\\", "/");
    }
    if (imageLoc.contains("//")) {
      imageLoc = imageLoc.replace("//", "/");
    }
    for (String itemImageLoc : itemImageLocs) {
      if (imageLoc.equals(itemImageLoc)) {
        sameAsCategoryItemsImg = true;
      }
    }

    if (imgFile.exists() && !sameAsCategoryImg && !sameAsCategoryItemsImg) { // only execute if imgLoc path is valid 
      if (this.currentCategory.equals("")) { // if on the home page
        // adds a new category with its name and image
        this.categoryImgsToCategoryNames.addItem(imageLoc, text);
        // sets up a place to put items of that category (for future item additions)
        this.categoryNamesToCategoryItems.set(text, new AACCategory(text));
      } else { // if in a category (page)
        AACCategory categoryAAC = this.getExceptionCatching(this.currentCategory);
        categoryAAC.addItem(imageLoc, text);
      }
    } else if (sameAsCategoryItemsImg) {
      pen.println("Adding was not successful. The item image cannot be a duplicate image.\n");
    } else if (sameAsCategoryImg) { 
      // The image for an item should not be the same as the image for the category it's in
      // b/c that causes problems with the getText method in this class, given the 
      // structures in the fields
      pen.println("Adding was not successful. The item image cannot be the same as the category image.\n");
    } else { // imageLoc path is not valid 
      pen.println("Adding was not successful. Image location was not a valid path.\n");
    }
  } // add(String, String)

  // +-----------------+---------------------------------------------
  // | Private Methods |
  // +-----------------+

  private AACCategory getExceptionCatching(String category) {
    try {
      return this.categoryNamesToCategoryItems.get(category);
    } catch (KeyNotFoundException knfe) {
      // Does nothing b/c category is guaranteed to be a key of this.categoryNamesToCategoryItems,
      // so KeyNotFoundException will never be caught here b/c as a helper fxn, the methods in 
      // this class that invoke it ensure this fact
      return null; // for the sake of returning something
    }
  } // getExceptionCatching()
} // class AACMappings