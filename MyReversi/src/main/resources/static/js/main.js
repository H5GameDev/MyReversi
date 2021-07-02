
function setCookie(cname, cvalue, exdays) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays*24*60*60*1000));
    var expires = "expires="+ d.toUTCString();
    document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
} 
function getCookie(cname) {
    var name = cname + "=";
    var decodedCookie = decodeURIComponent(document.cookie);
    var ca = decodedCookie.split(';');
    for(var i = 0; i <ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
         }
         if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
         }
     }
    return "";
} 
var base_url = "http://localhost:8080/MyReversi/"
var uid = "";
var sid = "";
var myside = "";
var bCount = 0;
var wCount = 0;
var boarddata = "";
var Board = new Array(8);
for(var i =0;i<8;i++){
	Board[i] = new Array(8);
	for(var j=0;j<8;j++)
		Board[i][j] = -1;
}
function updateChessboard(boardData){
	boarddata = boardData;
	if(boardData == null)return;
	if(boardData == "")return;
	for(var i =0;i<8;i++){
		for(var j=0;j<8;j++){
			switch(boardData.charAt(2*j+i*16)){
				case '^':Board[j][i] = -1;break;
				case 'B':Board[j][i] = 0;break;
				case 'W':Board[j][i] = 1;break;
			}
		}
	}

}
function login(){
	$.get(base_url + 'login',{},function(data){
		setCookie("uid",data,1);
		uid = data;
	},'text');
}
var currentSide = -1;
function gs(){
	
	if(uid == ""){
		login();
		return;
	}else{
		$.getJSON(base_url + "gs",{'uid':uid, 'sid':sid},function(data){
			if(data['status']>=0){
				switch(data['status']){
					case 0:
						//alert("Not enough player");
						break;
					case 1:
						//alert("Wait for another player to be ready");
						break;
					case 2:
						//alert("Game start!");

						updateChessboard(data['gstatus']['chessBoard']);
						bCount = data['gstatus']['bCount'];
						wCount = data['gstatus']['wCount'];
						currentSide = data['gstatus']['currentSide'] == "BLACK"? 0 : 1;
						break;
					
				}
				side = data['side'];

			}else{
				switch(data['status']){
					case -1:
						//alert("UID NOT VALID");
						uid = "";
						break;
					case -2:
						//alert("ROOM FULL");
						break;
					case -3:
						//alert("ROOM ID NOT VALID");
						break;
					default:break;
				}
				sid = "";
			}
		});
	}

}
function play(x,y){

	if(uid == ""){
		login();
		return;
	}else{
		$.getJSON(base_url + "play",{'uid':uid, 'sid':sid,'posx':y,'posy':x},function(data){
			if(data['status']>=0){
				if(data['status']==3){
					alert("GAME END"+ data['message']);
				}
			}
			else{
				switch(data['status']){
					case -1:alert("UID NOT VALID");uid = "";break;
					case -2:alert("ROOM FULL");sid = "";break;
					case -3:alert("ROOM ID NOT VALID");sid = "";break;
					case -4:alert("NOT VALID MOVE");break;
					default:break;
				}
				
			}
		});
	}
}
function join(){
	
	if(uid == ""){
		login();
		return;
	}else{
		$.getJSON(base_url + "join",{'uid':uid, 'sid':sid},function(data){
			if(data['status']>=0){
				alert("Joined Room "+sid);

			}else{
				switch(data['status']){
					case -1:alert("UID NOT VALID");uid = "";break;
					case -2:alert("ROOM FULL");break;
					case -3:alert("ROOM ID NOT VALID");break;
					default:break;
				}
				sid = "";
			}
		});
	}
}
function ready(postCallback){
	
	if(uid == ""){
		login();
		return;
	}else{
		$.getJSON(base_url + "ready",{'uid':uid, 'sid':sid},function(data){
			if(data['status']>=0){
				switch(data['status']){
					case 0:
						alert("Not enough player");break;
					case 1:
						alert("Wait for another player to be ready");break;
					case 2:
						alert("Game start!");
						myside = data['userSide'];
						updateChessboard(data['gstatus']['chessBoard']);

						break;
					
				}
				myside = data['userSide'];

			}else{
				switch(data['status']){
					case -1:alert("UID NOT VALID");uid = "";break;
					case -2:alert("ROOM FULL");break;
					case -3:alert("ROOM ID NOT VALID");break;
					default:break;
				}
				sid = "";
			}
			postCallback();
		});
	}
}
