package view.khachhang;

import dao.*;
import java.sql.*;
import model.*;

public class VariantDetailUI6 {
    private final Connection con;
    private final Account6 sessionAccount;
    private String variantId;
    private String productId;
    private String keyword="";
    private int page=1;
    private ProductVariant6 outVariantDetail;
    private int availableQty;
    private String outMessage="";
    public VariantDetailUI6(Connection con,Account6 sessionAccount){

        this.con=con;this.sessionAccount=sessionAccount;
    }
    public void showDetail()throws SQLException{
        outVariantDetail=null;availableQty=0;outMessage="";
        ProductVariant6 variant=new ProductVariantDAO6(con,sessionAccount).findActiveById(variantId);
        if(variant==null || productId==null || !productId.equals(variant.getProduct().getProductId())){
            outMessage="Biến thể không tồn tại, đã ngừng kinh doanh hoặc không thuộc mặt hàng được chọn.";return;
        }
        Product6 product=new ProductDAO6(con,sessionAccount).findActiveById(productId);
        if(product==null){outMessage="Mặt hàng không còn kinh doanh.";return;}
        variant.setProduct(product);
        availableQty=new InventoryDAO6(con,sessionAccount).getAvailableQty(variantId);
        outVariantDetail=variant;
    }
    public String getVariantId(){return variantId;}
    public void setVariantId(String value){variantId=value;}
    public String getProductId(){return productId;}
    public void setProductId(String value){productId=value;}
    public String getKeyword(){return keyword;}
    public void setKeyword(String value){keyword=value==null?"":value;}
    public int getPage(){return page;}
    public void setPage(int value){page=Math.max(1,value);}
    public ProductVariant6 getOutVariantDetail(){return outVariantDetail;}
    public int getAvailableQty(){return availableQty;}
    public String getOutMessage(){return outMessage;}
}
