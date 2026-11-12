package view.khachhang;

import dao.*;
import java.sql.*;
import java.util.*;
import model.*;

public class ProductDetailUI6 {
    private final Connection con;
    private final Account6 sessionAccount;
    private String productId;
    private String keyword="";
    private int page=1;
    private Product6 outProductDetail;
    private List<ProductVariant6> outVariantList=new ArrayList<>();
    private final Map<String,Integer> availableQuantities=new HashMap<>();
    private String outMessage="";
    public ProductDetailUI6(Connection con, Account6 sessionAccount) {

        this.con=con;this.sessionAccount=sessionAccount;
    }
    public void showProduct() throws SQLException {
        outProductDetail=null;outVariantList=new ArrayList<>();availableQuantities.clear();outMessage="";
        outProductDetail=new ProductDAO6(con,sessionAccount).findActiveById(productId);
        if(outProductDetail==null){outMessage="Mặt hàng không tồn tại hoặc không còn kinh doanh.";return;}
        outProductDetail.setCategory(new CategoryDAO6(con,sessionAccount)
                .findById(outProductDetail.getCategory().getCategoryId()));
        outVariantList=new ProductVariantDAO6(con,sessionAccount).findActiveByProduct(productId);
        outProductDetail.setVariants(outVariantList);
        InventoryDAO6 inventoryDAO=new InventoryDAO6(con,sessionAccount);
        for(ProductVariant6 variant:outVariantList)
            availableQuantities.put(variant.getVariantId(),inventoryDAO.getAvailableQty(variant.getVariantId()));
    }
    public String getProductId(){return productId;}
    public void setProductId(String value){productId=value;}
    public String getKeyword(){return keyword;}
    public void setKeyword(String value){keyword=value==null?"":value;}
    public int getPage(){return page;}
    public void setPage(int value){page=Math.max(1,value);}
    public Product6 getOutProductDetail(){return outProductDetail;}
    public List<ProductVariant6> getOutVariantList(){return outVariantList;}
    public int getAvailableQty(String variantId){Integer n=availableQuantities.get(variantId);return n==null?0:n;}
    public String getOutMessage(){return outMessage;}
}
