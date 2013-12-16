package brain.models


case class Information(val id:String, val name:String, val knowledgeId:String){
    
//    def getTeachings:Set[Teaching]={
//        
//    }
//	def getKnowledges:Set[Knowledge]
    
    def toJson:String={
        raw"{id:'$id', name:'$name', knowledgeId:'$knowledgeId'}"
    }
}

