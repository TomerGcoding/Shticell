package cell.numbers;
import cell.Cell;

public class NumCell extends Cell {
   Double value;

   //gets Double object, No need valueOf
   public NumCell() {};
   public NumCell (Double num) {value = num;}

   @Override
   public String toString() {
      if (value == null) {
         return "null";
      }

      // Check if the value is an integer
      if (isInt()) {
         return String.format("%.0f", value);  // Format as integer
      } else {
         return String.format("%.2f", value);  // Format with 2 decimal places
      }
   }
   protected Boolean isInt() {return value % 1 == value;}

   @Override
   public Double getOriginalValue() {return isInt()? value.intValue() : value;}

   @Override
   public Double getEffectiveValue() {return isInt()? value.intValue() : value;}

}
