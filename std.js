importPackage(org.sapid.checker.core);

var Config = {
 "PLUGIN_ID" : "JsRule"
};

var Marker = {
    add : function(o){
	if(o.lineno){
	    var r = new Result(Config.PLUGIN_ID,
			       o.lineno,
			       3,
			       (o.message || "js rules"));
	    results.add(r);
	}else{
	}
    },
    reset : function(){
	results.clear();
    }
}