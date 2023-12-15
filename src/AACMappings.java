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
   * @pre The file exists
   * @pre The file is formatted as the text location of the category followed by the text 
   * name of the category and then one line per item in the category that starts with > \
   * and then has the file name and text of that image
   * @post all images and their associated texts load in properly on the AAC GUI
   */
  public AACMappings(String filename) {
    // has to parse file for info
    PrintWriter pen = new PrintWriter(System.out, true);
    
    try {  
      // Reads from the specified file
      BufferedReader reader = new BufferedReader(new FileReader(filename));
      this.categoryImgsToCategoryNames = new AACCategory(""); // "" for home page
      this.categoryNamesToCategoryItems = new AssociativeArray<String,AACCategory>();
      String line;
      String currentReadCategory = ""; // for later when adding items to the category

      // Keep looping while the end of file hasn't been reached
      while((line = reader.readLine()) != null) { 
        String[] words = line.split(" ");

        // If the words on each line in the file is less than 2, then that means either the 
        // image, text, or both are missing in a line defining the category or an item
        if (words.length < 2) {
          pen.printf("Error: Format of file input in %s was wrong. " + 
              "Please fix the text formatting of the file before running again.\n", filename);
          System.exit(3);
        } // if

        if (line.charAt(0) != '>') { // This line has category img and category name
          String categoryImgLoc = words[0];
          String categoryName = words[1];

          currentReadCategory = categoryName;
          // Adds the category's image and its name to the array of all categories (home page)
          this.categoryImgsToCategoryNames.addItem(categoryImgLoc, categoryName);

          // Creates new category
          AACCategory categoryItems = new AACCategory(currentReadCategory);
          // Adds new category to array of all categories
          this.categoryNamesToCategoryItems.set(currentReadCategory, categoryItems);
        } else { // This line has item img and item text
          String itemImgLoc = words[0].substring(1);
          String itemName = "";

          // Determine the item text
          for (int i = 1; i < words.length; i++) { // In case the item name is multiple words
            itemName += words[i] + " ";
          } // for
          itemName = itemName.trim();

          // Adds the item's image and name to the specified category in the array detailing 
          // all categories' items
          AACCategory categoryArr = this.getExceptionCatching(currentReadCategory);
          categoryArr.addItem(itemImgLoc, itemName);
        } // if/else
      } // while
      reader.close();
    } catch (FileNotFoundException e) {// if the file wasn't found
      pen.printf("Error: The file %s was not found.\n", filename);
      System.exit(1);
    } catch (IOException e) { // some other input/output error
      pen.println(e.getMessage());
      System.exit(3);
    } // try/catch
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
   * @pre `imageLoc` is an image in AACMappings
   * @post associated text of `imageLoc` in AACMappings is returned
   */
  public String getText(String imageLoc) {
    // For when imageLoc is not in AAC Mappings
    String text = "Error: " + imageLoc + " does not have an associated text";

    // If the image represents a category
    if (this.currentCategory.equals("") && categoryImgsToCategoryNames.hasImage(imageLoc)) {
      // Retrieve the category name and set the current category to that category
      String categoryName = categoryImgsToCategoryNames.getText(imageLoc);
      this.currentCategory = categoryName;
      text = categoryName;
    } else {
      // Otherwise, the image represents an item (in a category)
      AACCategory categoryAAC = this.getExceptionCatching(this.currentCategory);

      // Get the text associated with the item image (only if there is one)
      if (categoryAAC.hasImage(imageLoc)) {
        text = categoryAAC.getText(imageLoc);
      } // if
    } // if/else
    return text;
  } // getText(String)
  
  /**
   * Provides an array of all the images in the current category
   * 
   * @return the array of images in the current category
   */
  public String[] getImageLocs() { 
    if (this.currentCategory.equals("")) { // If the current category is the home page,
      return this.categoryImgsToCategoryNames.getImages(); // get all category images
    } else { // If the current category is not the home page,
      return this.getExceptionCatching(this.currentCategory).getImages(); // get all item images
    } // if/else
  } // getImageLocs()

  /**
   * Resets the current category of the AAC back to the default category
   */
  public void reset() {
    this.currentCategory = ""; // "" represents the home page (default category)
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
   * @pre `imageLoc` is an img in AACMappings
   * @post returns true or false (with no printed "error" message)
   */
  public boolean isCategory(String imageLoc) { 
    boolean result = false;

    if (this.categoryImgsToCategoryNames.hasImage(imageLoc)) { // If the image is a category image,
      result = true; // Image represents a category
    } else { // Otherwise, the image should be an item image
      // Still need to check if imageLoc even exists as an item image
      String[] categories = this.categoryNamesToCategoryItems.getKeysForAAC();
      boolean existsAsItem = false;

      // For each category, iterate through all the item images and check if imageLoc
      // is one of them
      for (String category : categories) {
        if (this.getExceptionCatching(category).hasImage(imageLoc)) {
          existsAsItem = true;
        } // if
      } // for

      if (!existsAsItem) { // When imageLoc is not part of AAC Mappings
        PrintWriter pen = new PrintWriter(System.out, true);
        pen.printf("Error: %s is not an image representing a text to speak nor " + 
                   "is it an image representing a category.\n", imageLoc);
      } // if
    } // if/else
    return result;
  } // isCategory(String)

  /**
   * Writes the AAC mappings stored to a file. The file is formatted as the text location of the 
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
      // Writes the current AAC mappings to the specified file 
      BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
      AACCategory categories = this.categoryImgsToCategoryNames;
      String[] categoryImgs = categories.getImages();

      // For each category image, find the category name and write the category image
      // and the category name to the file 
      for (String categoryImg : categoryImgs) {
        String categoryName = categories.getText(categoryImg);
        writer.write(categoryImg + " " + categoryName + "\n");

        AACCategory itemsInCategory = this.getExceptionCatching(categoryName);
        String[] itemImages = itemsInCategory.getImages();

        // For each item image in the same category, find the item text and write the 
        // item image and the item text to the file with a ">" at the beginning of the line
        for (String itemImage : itemImages) {
          String itemName = itemsInCategory.getText(itemImage);
          writer.write(">" + itemImage + " " + itemName + "\n");
        } // for
      } // for
      // closes writer
      writer.close(); 
    } catch (IOException e) {
      pen.println(e.getMessage());
    } // try/catch
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
    boolean sameAsCategoryItemsImg = false;
    PrintWriter pen = new PrintWriter(System.out, true);
    String[] itemImageLocs = this.getImageLocs();

    // For portable code (converting format of file paths)
    if (imageLoc.contains("\\")) {
      imageLoc = imageLoc.replace("\\", "/");
    } // if

    if (imageLoc.contains("//")) {
      imageLoc = imageLoc.replace("//", "/");
    } // if

    if (!imgFile.exists()) { // imageLoc path is not valid 
      pen.printf("Error: %s, the image location, is not a valid path. " + 
          "Adding was not successful.\n", imageLoc);
    } else if (this.currentCategory.equals("")) { // Trying to add a category (on home page)
      // Check home page for conflicts
      if (this.categoryImgsToCategoryNames.hasImage(imageLoc)) { 
        // If the image is a duplicate of a category image, don't add the new category b/c
        // duplicates cause conflicts with the structures given in the fields of this class
        pen.println("Error: This category image cannot be a duplicated image of an existing" + 
            " category image in the home page. Adding was not successful.");
      } else { 
        // Otherwise, the image is not a duplicate category image, and the new category
        // can be successfully added

        // Add a new category with specified name and image
        this.categoryImgsToCategoryNames.addItem(imageLoc, text);
        // and set up an AACCategory to put items of that category (for future item additions)
        this.categoryNamesToCategoryItems.set(text, new AACCategory(text));
      } // if/else
    } else { // Trying to add an item (on category page)
      // Checking if imageLoc is a duplicate item image
      for (String itemImageLoc : itemImageLocs) {
        if (imageLoc.equals(itemImageLoc)) {
          sameAsCategoryItemsImg = true;
        } // if
      } // for
      
      // Retrieving img for current category (for error-checking later)
      String[] allCategoryImgs = this.categoryImgsToCategoryNames.getImages();
      String currentCategoryImg = "";

      // Loops through each category img to find which one is the current category's img 
      // (according to if their names match)
      for (String categoryImg : allCategoryImgs) {
        if (this.categoryImgsToCategoryNames.getText(categoryImg).equals(this.currentCategory)) {
          currentCategoryImg = categoryImg;
        } // if
      } // for
      
      // Error-checking (b/c some duplicate images (2 categories with the same img, an item in 
      // a category with the same img as the category its in, and two items in the same category
      // with the same imgs) will not work well with the structures in AACMappings)
      if (imageLoc.equals(currentCategoryImg)) { // Check current category's image for conflicts
        // If the image is a duplicate of its category's image
        pen.println("Error: This item image cannot be a duplicated image of its category's" + 
            " image. Adding was not successful.");
      } else if (sameAsCategoryItemsImg) { // Check items for conflicts
        pen.println("Error: This item image cannot be a duplicated image of another item image" + 
            " in the same category. Adding was not successful.");
      } else { 
        // Otherwise, the image is not a duplicate image, and the new item can be 
        // successfully added

        // Add the specified text and image as an item to the category
        AACCategory categoryAAC = this.getExceptionCatching(this.currentCategory);
        categoryAAC.addItem(imageLoc, text);
      } // if/else
    } // if/else
  } // add(String, String)

  // +-----------------+---------------------------------------------
  // | Private Methods |
  // +-----------------+

  /**
   * Retrieves the AACCategory of the specified category while resolving
   * the exception thrown warning from the `get` method
   * 
   * @param category
   * @return AACCategory associated with category
   * @pre `category` is a category in AACMappings
   * @post returns the AACCategory for specified category
   */
  private AACCategory getExceptionCatching(String category) {
    try { 
    // Return the AACCategory (contains info about the category's items) for the specified category
      return this.categoryNamesToCategoryItems.get(category);
    } catch (KeyNotFoundException knfe) {
      // Does nothing b/c category is guaranteed to be a key of this.categoryNamesToCategoryItems,
      // so KeyNotFoundException will never be caught here b/c as a helper fxn, the methods in 
      // this class that invoke it ensure this fact
      return null; // for the sake of returning something
    } // try/catch
  } // getExceptionCatching()
} // class AACMappings