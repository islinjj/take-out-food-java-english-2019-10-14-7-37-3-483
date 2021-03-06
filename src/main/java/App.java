import java.util.*;

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
public class App {
    private ItemRepository itemRepository;
    private SalesPromotionRepository salesPromotionRepository;

    public App(ItemRepository itemRepository, SalesPromotionRepository salesPromotionRepository) {
        this.itemRepository = itemRepository;
        this.salesPromotionRepository = salesPromotionRepository;
    }

    public String bestCharge(List<String> inputs) {
        //TODO: write code here
//        获取所有的菜品,优惠活动
        List<Item> items = itemRepository.findAll();
        List<SalesPromotion> salesPromotions = salesPromotionRepository.findAll();
//        结果
        String result = "============= Order details =============\n";
//        未优惠的总价格
        double totalPrice = 0;
//        省的钱
        int savingMoney = 0;
//        半价省的钱
        int halfSavingMoney = 0;
        List<Item> myItems = new ArrayList<>();
//        记录各菜品所选的数量
        Map<String,Integer> amountMap = new HashMap<>();
//        记录用户选择的半价商品的条目
        List<String> userHalfItems = new ArrayList<>();
//        根据条件获取规定的半价商品条目
        Optional<SalesPromotion> salesPromotionOptional = salesPromotions.stream().filter(p -> p.getType().equals("50%_DISCOUNT_ON_SPECIFIED_ITEMS")).findFirst();
        SalesPromotion salesPromotion = salesPromotionOptional.get();
        List<String> halfItems = salesPromotion.getRelatedItems();

        //计算总价格
        for (String input : inputs) {
            String itemId = input.substring(0,input.indexOf(" "));
            int amount = Integer.parseInt(input.substring(input.indexOf("x")+2));
            amountMap.put(itemId,amount);
//            根据菜品ID获取菜品
            Optional<Item> itemOptional = items.stream().filter(p -> p.getId().equals(itemId)).findFirst();
            Item item = itemOptional.get();
            myItems.add(item);//作为输出结果进行遍历

            totalPrice += item.getPrice() * amount;//总价格
//            该菜品为可半价优惠的菜品,则计算优惠价格
            if (halfItems.contains(itemId)){
                halfSavingMoney += item.getPrice() * amount/2;
                userHalfItems.add(item.getName());
            }
        }
//        优惠信息
        String promotionInfo = "";
//        计算满30优惠
        if (totalPrice >= 30){
            savingMoney = 6;
            promotionInfo = "Promotion used:\n" +
                        "满30减6 yuan，saving 6 yuan\n" +
                        "-----------------------------------\n";
        }

//        返回结果
        String itemNameAndAmountAndPrice = "";
        for (Item myItem : myItems) {
            String name = myItem.getName();
            int itemAmount = amountMap.get(myItem.getId());
            double singleItemTotal = myItem.getPrice() * itemAmount;
            itemNameAndAmountAndPrice += name + " x "+ itemAmount + " = " + (int)singleItemTotal + " yuan\n";
        }
        result += itemNameAndAmountAndPrice;

//        客户选择的半价商品
        String halfStr = userHalfItems.toString().substring(1,userHalfItems.toString().length()-1).replaceAll(", ","，");
        result += "-----------------------------------\n";
        //        判断两个优惠
        if (savingMoney < halfSavingMoney){ //半价优惠过满30
            savingMoney = halfSavingMoney;
            promotionInfo = "Promotion used:\n" +
                    "Half price for certain dishes (" + halfStr + ")，saving " + savingMoney + " yuan\n" +
                    "-----------------------------------\n";
        }
        result += promotionInfo;
        result += "Total：" + (int)(totalPrice-savingMoney) + " yuan\n" +
                  "===================================";
        return result;
    }
}
