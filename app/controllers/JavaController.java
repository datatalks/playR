package controllers;

  import play.*;
  import play.mvc.*;

  import java.io.File;
  import java.io.FileInputStream;

  import com.alibaba.idst.nls.NlsClient;
  import com.alibaba.idst.nls.NlsFuture;
  import com.alibaba.idst.nls.event.NlsEvent;
  import com.alibaba.idst.nls.event.NlsListener;
  import com.alibaba.idst.nls.protocol.NlsRequest;
  import com.alibaba.idst.nls.protocol.NlsResponse;


public class JavaController extends Controller {

  public Result test() {
    return ok("this is the java Actions!!!!!!");
  }

}
