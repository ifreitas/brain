package brain.snippet

import net.liftweb._ 
import http._ 
import util.Helpers._

/**
 * 
 * Example:
 * <div data-lift="modal?id=anId&title=Some text to title">
 *   <div data-lift="">?????
 * </div>
 */
class Modal{
    
    var id:String = S.attr("id") getOrElse null
    var title:String = S.attr("title") getOrElse null
    
	def render = {
	    require(id != null && !id.isEmpty, "Modal id is required.")
	    val labelledBy = id+"Label"
	    
        <div class="modal fade" id={id} tabindex="-1" role="dialog" aria-labelledby={labelledBy} aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                        <h4 class="modal-title" id={labelledBy}>{title}</h4>
                    </div>
                    <div class="modal-body"></div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                        <button type="button" class="btn btn-primary">Save changes</button>
                    </div>
                </div>
                <!-- /.modal-content -->
            </div>
            <!-- /.modal-dialog -->
        </div>
	}
}

class ModalBody{
    def render={
        "*" #> "*"
    }
}
