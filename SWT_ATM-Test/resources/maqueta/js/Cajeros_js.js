		function valida_withdrawal( mult,mult2){
        
			var numero=$('#write').val();
			if(numero==''){
//----------------------------------------------------------------------------------------
sayErroneousAmount();
//----------------------------------------------------------------------------------------
				$('.mesageInfo2').fadeIn();
			}else{
				if ((numero%mult!=0)&&(numero%mult2!=0)){
//----------------------------------------------------------------------------------------
sayErroneousAmount();
//----------------------------------------------------------------------------------------
						$('.mesageInfo2').fadeIn();
				}else{
					$('.quickbalancefoot').animate({
						left: '-1000px',
					},500);
					setTimeout(function(){$('.balanceclick').removeClass('quickon')}, 400);
					$('.slider').css('overflow','auto');
					$('.slider').css('position','static');
					$('.box_shadow').css('position','relative');
					$('.moreoptions').fadeOut(300, 'linear');
					$('.previousptions').fadeOut(300, 'linear');
					$('.posicionslider').fadeOut(300, 'linear');
					$('.footback').fadeOut(300, 'linear');
					setTimeout(function(){$('.buttongrey').fadeOut(600, 'linear');}, 300);
					
					
					setTimeout(function(){
						$('.box_shadow').animate({
							top: '-1000',
						},500);
					}, 300);
					setTimeout(function(){window.location = '01_withdrawal_4.htm';}, 200);
					
                    return false;
				}
			}
		}
		function valida_transfer( mult,mult2){
					setTimeout(function(){
						$('.box_shadow').animate({
							top: '-1000',
						},500);
					}, 300);
					setTimeout(function(){window.location = 'transfer_c0.htm';}, 200);
					return false();
		}
		function valida_deposit( mult,mult2){
					setTimeout(function(){
						$('.box_shadow').animate({
							top: '-1000',
						},500);
					}, 300);
					setTimeout(function(){window.location = 'deposito_6.htm';}, 200);
					return false();
		}
		function compruebadigitos(){
			var longitud = $("#write").val().length;
			if(longitud>=3){
				$('.actions_fastbox').slideDown();
				event.preventDefault();
			}
		}
		$(document).ready(function() {
			$('.bloque_form input').focus();
			$('input#write').focus();
			$('input#write').keypress(
				function() {
					compruebadigitos();
				}
			);
			
			$('.keyboard .symbol').click(function() {
				var key=$(this).children().text();
				var inputvalor = $('#write').val();
				var totalvalor= inputvalor + key;
				if($('#write').val().length<2){
					$('#write').val(totalvalor);
					if($(this).hasClass('zero')){
						$('.inputfalsed').val('1');
					}
				}else if($('#write').val().length==2){
					var division= totalvalor /100;
					var restval = totalvalor.length;
					var rest = totalvalor.slice(0,restval-1);
					var total=totalvalor-rest;

					
					if($(this).hasClass('zero')){
						if($('.inputfalsed').val()==1){
							$('#write').val(division + '.00');
						}else{
							$('#write').val(division + '0');
						}
						$('.inputfalsed').val('2');
					}else{
						$('#write').val(division);
					}
				}else if($('#write').val().length>2){
					var falsedvalue =$('.inputfalsed').val()
					var multiple= totalvalor * 1000;
					var newdivision = multiple /100;
					if($(this).hasClass('zero')){
						if(falsedvalue==''){
							$('#write').val(newdivision + '0');
							$('.inputfalsed').val('2');
						}else{
							$('#write').val(newdivision + '.00');
						}	
					}else{
						$('.inputfalsed').val('')
						$('#write').val(newdivision);
					}
				}
                
//----------------------------------------------------------------------------------------
sayMoneyAmount($('#write').val());
//----------------------------------------------------------------------------------------
                
			});
			
			$('.borrarcontenido').click(function() {
				$('#write').val('');
				$('.inputfalsed').val('');
				$('.mesageInfo2').fadeOut();
			});
			$('.balanceclick').click(function() {
				if($(this).hasClass('quickon')){
					$('.foot .settings').fadeIn();
					$('.quickbalancefoot').animate({
						left: '-1000px',
					},300);
					setTimeout(function(){$('.balanceclick').removeClass('quickon')}, 200);
				}else{
					$('.foot .settings').fadeOut(200,'linear');
					$('.foot .mesageInfo').fadeOut(200,'linear');
					$(this).addClass('quickon');
					$('.quickbalancefoot').animate({
						left: '129px',
					},300);
				}
				
			});
			$('.navigation .next').click(function() {
				$('.quickbalancefoot .carousel').animate({
					left: '-346px',
				},300);
			});
			$('.navigation .prev').click(function() {
				$('.quickbalancefoot .carousel').animate({
					left: '0',
				},300);
			});
			
			
			$('#right-but').click(function() {
				$('.zoomdiv').fadeOut(100,'linear');
				setTimeout(function(){
					$('.zoomdiv').fadeIn(300,'linear');
				}, 1000);
			});
			$('#left-but').click(function() {
				$('.zoomdiv').fadeOut(100,'linear');
				setTimeout(function(){
					$('.zoomdiv').fadeIn(300,'linear');
				}, 1000);
			});
		
			
			
			
			

			$('.actions_fastbox').hide();
			$('.box_shadow3').hide();
			$('.cloudcarouselfoot .operations_foot_edit').hide();
			$('.mesageInfo2').hide();
			$('.degradadoright').hide();
			$('.degradadoright').fadeIn();
			$('.mesageInfo').hide();
			$('.Next_screen').hide();
			$('.degradadoleft').hide();
			$('.nextscreenfoot').hide();
			$('.footback').hide();
			$('.buttongrey').hide();
			$('.home .box_shadow').hide();
			setTimeout(function(){$('.mesageInfo').fadeIn();}, 1000);
			
			$('.box_shadow3').fadeIn();
			setTimeout(function(){$('.numeric_keyboard').fadeIn(500,'linear')}, 500);
			
			$('.poptions').hide();
			setTimeout(function(){$('.mesageInfo').fadeOut()}, 5000);
			setTimeout(function(){$('.footback').fadeIn()}, 500);
			setTimeout(function(){$('.buttongrey').fadeIn()}, 500);
			$('.withdrawal .box_shadow').animate({
				top: '0',
			},500);
			$('.box_shadow2').animate({
				top: '10px',
			},500);
			$('.box_shadow2').animate({
				top: '10px',
			},500);
			$('.verticalcarousel').animate({
				top: '-1000',
			},30000);
			
			/*Withdrawal*/
			
				$('.withdrawal .box').click(function() {
					$('.footback').fadeOut(200, 'linear');
				});
				
				$('.cancel_process').click(function() {
					$('.inputEdit input').val('');
				});
				
				
				$('.editamount').click(function() {
					$('.cloudcarouselfoot .operations_foot').hide();
					$('.cloudcarouselfoot .operations_foot_edit').fadeIn();
				});
				$('.ok_process').click(function() {
					$('.cloudcarouselfoot .operations_foot_edit').hide();
					$('.cloudcarouselfoot .operations_foot').fadeIn();
				});
				
				
			
			/*Scanning Ckeck*/			
			$('.loadingg .check').animate({
				width: '532px',
			},1000);
			
			
			
			
			$('.slider1').live('swipeleft swiperight',function(event){
				if (event.type == "swiperight") {
					$('.slider1').animate({
					left: '0'
					},800);
					$('.slider2').animate({
						left: '0'
					},800);
					$('.slider3').animate({
						left: '0'
					},1050);
					$('.moptions').fadeIn();
					$('.poptions').fadeOut();
					$('.degradadoleft').hide();
					$('.degradadoright').show();
					if($(this).css('left')!='0px'){
						$('.icono_activo').addClass('prevclass');
						$('.icono_activo').prev().addClass('icono_activo');
						$('.prevclass').removeClass('icono_activo');
						$('.prevclass').removeClass('prevclass');
					}
					
					
				}
				if (event.type == "swipeleft") {
					$('.moptions').fadeOut();
					$('.poptions').fadeIn();
					$('.slider1').animate({
					left: '-862px'
					},800);
					$('.slider2').animate({
						left: '-862px'
					},800);
					$('.slider3').animate({
						left: '-862px'
					},1050);
					if($(this).css('left')!='-862px'){
						$('.degradadoleft').show();
						$('.degradadoright').hide();
						$('.icono_activo').addClass('prevclass');
						$('.icono_activo').next().addClass('icono_activo');
						$('.prevclass').removeClass('icono_activo');
						$('.prevclass').removeClass('prevclass');
					}
				}
			});
			$('.slider2').live('swipeleft swiperight',function(event){
				if (event.type == "swiperight") {
					$('.slider1').animate({
					left: '0'
					},800);
					$('.slider2').animate({
						left: '0'
					},800);
					$('.slider3').animate({
						left: '0'
					},1050);
					$('.moptions').fadeIn();
					$('.poptions').fadeOut();
					$('.degradadoleft').hide();
					$('.degradadoright').show();
					if($(this).css('left')!='0px'){
						$('.icono_activo').addClass('prevclass');
						$('.icono_activo').prev().addClass('icono_activo');
						$('.prevclass').removeClass('icono_activo');
						$('.prevclass').removeClass('prevclass');
					}
					
					
				}
				if (event.type == "swipeleft") {
					$('.moptions').fadeOut();
					$('.poptions').fadeIn();
					$('.slider1').animate({
					left: '-862px'
					},800);
					$('.slider2').animate({
						left: '-862px'
					},800);
					$('.slider3').animate({
						left: '-862px'
					},1050);
					if($(this).css('left')!='-862px'){
						$('.degradadoleft').show();
						$('.degradadoright').hide();
						$('.icono_activo').addClass('prevclass');
						$('.icono_activo').next().addClass('icono_activo');
						$('.prevclass').removeClass('icono_activo');
						$('.prevclass').removeClass('prevclass');
					}
				}
			});
			$('.slider3').live('swipeleft swiperight',function(event){
				if (event.type == "swiperight") {
					$('.slider1').animate({
					left: '0'
					},800);
					$('.slider2').animate({
						left: '0'
					},800);
					$('.slider3').animate({
						left: '0'
					},1050);
					$('.moptions').fadeIn();
					$('.poptions').fadeOut();
					$('.degradadoleft').hide();
					$('.degradadoright').show();
					if($(this).css('left')!='0px'){
						$('.icono_activo').addClass('prevclass');
						$('.icono_activo').prev().addClass('icono_activo');
						$('.prevclass').removeClass('icono_activo');
						$('.prevclass').removeClass('prevclass');
					}
					
					
				}
				if (event.type == "swipeleft") {
					$('.moptions').fadeOut();
					$('.poptions').fadeIn();
					$('.slider1').animate({
					left: '-862px'
					},800);
					$('.slider2').animate({
						left: '-862px'
					},800);
					$('.slider3').animate({
						left: '-862px'
					},1050);
					if($(this).css('left')!='-862px'){
						$('.degradadoleft').show();
						$('.degradadoright').hide();
						$('.icono_activo').addClass('prevclass');
						$('.icono_activo').next().addClass('icono_activo');
						$('.prevclass').removeClass('icono_activo');
						$('.prevclass').removeClass('prevclass');
					}
				}
			});
			
			
			$('.moreoptions').click(function() {
				$('.moptions').fadeOut();
				$('.poptions').fadeIn();
				$('.slider1').animate({
					left: '-862px'
				},800);
				$('.slider2').animate({
					left: '-862px'
				},800);
				$('.slider3').animate({
					left: '-862px'
				},1050);
				$('.degradadoleft').show();
				$('.degradadoright').hide();
				$('.icono_activo').addClass('prevclass');
				$('.icono_activo').next().addClass('icono_activo');
				$('.prevclass').removeClass('icono_activo');
				$('.prevclass').removeClass('prevclass');
			});
			
			$('.previousptions').click(function() {
				$('.moptions').fadeIn();
				$('.poptions').fadeOut();
				$('.slider1').animate({
					left: '0'
				},800);
				$('.slider2').animate({
					left: '0'
				},800);
				$('.slider3').animate({
					left: '0'
				},1050);
				$('.degradadoleft').hide();
					$('.degradadoright').show();
				
				$('.icono_activo').prev().addClass('primerpaso');
				$('.icono_activo').removeClass('icono_activo');
				$('.primerpaso').addClass('icono_activo');
				$('.prevclass').removeClass('primerpaso');
			});
			
			$('.box').click(function() {
				$(this).addClass('boxactive');
				$('.slider').css('overflow','auto');
				$('.box_top').css('position','static');
				$('.slider').css('position','static');
				$('.box_shadow').css('position','relative');
				$('.box_shadow2').css('position','relative');
				$('.moreoptions').fadeOut(100, 'linear');
				$('.previousptions').fadeOut(100, 'linear');
				$('.posicionslider').fadeOut(100, 'linear');
				
					$('.quickbalancefoot').animate({
						left: '-1000px',
					},300);
					setTimeout(function(){$('.balanceclick').removeClass('quickon')}, 200);
				setTimeout(function(){
					$('.box_shadow').animate({
						top: '-1000',
					},500);
					$('.box_shadow2').animate({
						top: '-1000',
					},500);
					$('.mesageInfo').fadeOut();
					$('.buttongrey').fadeOut();
				}, 200);
				
				
			});
			
			$('.box_shadow3 .box').click(function() {
				$('.box_shadow3').fadeOut();
			});	
			
			$('.changelanguage').click(function() {
				setTimeout(function(){$('.box_shadow3').fadeOut();}, 500);
				setTimeout(function(){window.location = 'Select_Language.html';}, 600);
			});	
			
			$('.back').click(function() {
				$(this).addClass('boxactive');
				$('.slider').css('overflow','auto');
				$('.box_top').css('position','static');
				$('.slider').css('position','static');
				$('.box_shadow').css('position','relative');
				$('.moreoptions').fadeOut(300, 'linear');
				$('.previousptions').fadeOut(300, 'linear');
				$('.posicionslider').fadeOut(300, 'linear');
				$('.back').fadeOut(300, 'linear');
				$('.buttongrey').fadeOut(300, 'linear');
				$('.quickbalancefoot').animate({
					left: '-1000px',
				},300);
				setTimeout(function(){$('.balanceclick').removeClass('quickon')}, 200);
				setTimeout(function(){
					$('.box_shadow').animate({
						top: '800',
					},500);
					$('.mesageInfo').fadeOut();
				}, 200);
			});
			$('.mainmenu').click(function() {
				$(this).addClass('boxactive');
				$('.slider').css('overflow','auto');
				$('.box_top').css('position','static');
				$('.slider').css('position','static');
				$('.box_shadow').css('position','relative');
				$('.moreoptions').fadeOut(300, 'linear');
				$('.previousptions').fadeOut(300, 'linear');
				$('.posicionslider').fadeOut(300, 'linear');
				$('.back').fadeOut(300, 'linear');
				$('.buttongrey').fadeOut(300, 'linear');
				$('.quickbalancefoot').animate({
					left: '-1000px',
				},300);
				setTimeout(function(){$('.balanceclick').removeClass('quickon')}, 200);
				setTimeout(function(){
					$('.box_shadow').animate({
						top: '800',
					},500);
					$('.mesageInfo').fadeOut();
				}, 200);
				setTimeout(function(){window.location = 'cajeros_home_back.html';}, 600);
			});
			
			$('.settings').click(function() {
				$('.slider').css('overflow','auto');
				$('.box_top').css('position','static');
				$('.slider').css('position','static');
				$('.box_shadow').css('position','relative');
				$('.box_shadow2').css('position','relative');
				$('.moreoptions').fadeOut(100, 'linear');
				$('.previousptions').fadeOut(100, 'linear');
				$('.posicionslider').fadeOut(100, 'linear');
				
					$('.quickbalancefoot').animate({
						left: '-1000px',
					},300);
					setTimeout(function(){$('.balanceclick').removeClass('quickon')}, 200);
				setTimeout(function(){
					$('.box_shadow').animate({
						top: '-1000',
					},500);
					$('.box_shadow2').animate({
						top: '-1000',
					},500);
					$('.mesageInfo').fadeOut();
					$('.buttongrey').fadeOut();
				}, 200);
				setTimeout(function(){window.location = 'Settings.html';}, 500);
			});
			$('.tohome').click(function() {
				setTimeout(function(){
					$('.box_shadow').animate({
						top: '800',
					},500);
				}, 200);
				setTimeout(function(){window.location = 'cajeros_home_back.html';}, 600);
			});
			
			$('.totalamount').click(function() {
				$('.quickbalancefoot').animate({
						left: '-1000px',
					},500);
					setTimeout(function(){$('.balanceclick').removeClass('quickon')}, 400);
					$('.slider').css('overflow','auto');
					$('.slider').css('position','static');
					$('.box_shadow').css('position','relative');
					$('.moreoptions').fadeOut(300, 'linear');
					$('.previousptions').fadeOut(300, 'linear');
					$('.posicionslider').fadeOut(300, 'linear');
					$('.footback').fadeOut(300, 'linear');
					setTimeout(function(){$('.buttongrey').fadeOut(600, 'linear');}, 300);
					
					
				setTimeout(function(){
					$('.box_shadow').animate({
						top: '-1000',
					},500);
				}, 200);
				setTimeout(function(){window.location = 'Transfer_4.html';}, 600);
			});
			
			
			
			$('.carouselmobil').live('swipeleft swiperight',function(event){
				if (event.type == "swiperight") {
					if($('.previouschecks').hasClass('prevchecks2')){
						$('.carouselmobil').animate({
							left: '-629px'
						},800);
						$('.mchecks').fadeIn();	
						$('.previouschecks').removeClass('prevchecks2');
					}else{
						$('.carouselmobil').animate({
							left: '0'
						},800);
						$('.pchecks').fadeOut();
						$('.morechecks').removeClass('morechecks2');
					}
					$('.icono_activo').addClass('prevclass');
					$('.prevclass').removeClass('icono_activo');
					$('.prevclass').prev().addClass('icono_activo');
					$('.prevclass').removeClass('prevclass');
				}
				if (event.type == "swipeleft") {
					if($('.morechecks').hasClass('morechecks2')){
						$('.carouselmobil').animate({
							left: '-1405px'
						},800);
						$('.morechecks').removeClass('morechecks2');
						$('.previouschecks').addClass('prevchecks2');
						$('.mchecks').fadeOut();
						$('.pchecks').fadeIn();
					}else{
						$('.morechecks').addClass('morechecks2');
						$('.carouselmobil').animate({
							left: '-629px'
						},800);
						$('.pchecks').fadeIn();
					}
					$('.icono_activo').addClass('prevclass');
					$('.prevclass').removeClass('icono_activo');
					$('.prevclass').next().addClass('icono_activo');
					$('.prevclass').removeClass('prevclass');
				}
			});
			
			
			
			
			
			
			
			
			
			
			
			
			$('.morechecks').click(function() {
				if($(this).hasClass('morechecks2')){
					$('.carouselmobil').animate({
						left: '-1405px'
					},800);
					$(this).removeClass('morechecks2');
					$('.previouschecks').addClass('prevchecks2');
					$('.mchecks').fadeOut();
					$('.pchecks').fadeIn();
				}else{
					$(this).addClass('morechecks2');
					$('.carouselmobil').animate({
						left: '-629px'
					},800);
					$('.pchecks').fadeIn();
				}
					$('.icono_activo').addClass('prevclass');
					$('.prevclass').removeClass('icono_activo');
					$('.prevclass').next().addClass('icono_activo');
					$('.prevclass').removeClass('prevclass');
					
			});
			$('.previouschecks').click(function() {
				if($(this).hasClass('prevchecks2')){
					$('.carouselmobil').animate({
						left: '-629px'
					},800);
					$('.mchecks').fadeIn();	
					$(this).removeClass('prevchecks2');
				}else{
					$('.carouselmobil').animate({
						left: '0'
					},800);
					$('.pchecks').fadeOut();
					$('.morechecks').removeClass('morechecks2');
				}
				
				$('.icono_activo').addClass('prevclass');
				$('.prevclass').removeClass('icono_activo');
				$('.prevclass').prev().addClass('icono_activo');
				$('.prevclass').removeClass('prevclass');
				
			});
			
		
			
			
			
			
			
			
		/*Navigations*/
			$('.toWithdrawal_1').click(function() {
				setTimeout(function(){window.location = 'Withdrawal_1.html';}, 500);
			});
			$('.toWithdrawal_2').click(function() {
				setTimeout(function(){window.location = 'Withdrawal_2.html';}, 500);
			});
			$('.toWithdrawal_3').click(function() {
				setTimeout(function(){window.location = 'Withdrawal_3.html';}, 500);
			});
			$('.toWithdrawal_4').click(function() {
				$('.box_shadow3').fadeOut(100, 'linear');
				setTimeout(function(){window.location = 'Withdrawal_4.html';}, 500);
			});
			$('.toWithdrawal_5').click(function() {
				setTimeout(function(){window.location = 'Withdrawal_4.html';}, 500);
			});
			$('.toWithdrawal_6').click(function() {
				setTimeout(function(){window.location = 'Withdrawal_6.html';}, 500);
			});
			$('.toWithdrawal_9').click(function() {
				setTimeout(function(){window.location = 'Withdrawal_9.html';}, 500);
			});
			$('.toWithdrawal_10').click(function() {
				setTimeout(function(){window.location = 'Withdrawal_10.html';}, 500);
			});
			
			$('.toTransfer_1').click(function() {
				setTimeout(function(){window.location = 'Transfer_1.html';}, 500);
			});
			$('.toTransfer_2').click(function() {
				setTimeout(function(){window.location = 'Transfer_2.html';}, 500);
			});
			$('.toTransfer_3').click(function() {
				setTimeout(function(){window.location = 'Transfer_3.html';}, 500);
			});
			$('.toTransfer_6').click(function() {
				setTimeout(function(){window.location = 'Transfer_6.html';}, 500);
			});
			$('.toTransfer_7').click(function() {
				setTimeout(function(){window.location = 'Transfer_7.html';}, 500);
			});
			$('.toTransfer_8').click(function() {
				setTimeout(function(){window.location = 'Transfer_8.html';}, 500);
			});
			
			$('.toBalance').click(function() {
				setTimeout(function(){window.location = 'Balances.html';}, 500);
			});
			$('.toDepositcash').click(function() {
				setTimeout(function(){window.location = 'Deposit_cash.html';}, 500);
			});
			
			
			$('.tofastcash_1').click(function() {
				setTimeout(function(){window.location = 'FastCash_1.html';}, 500);
			});
			$('.tofastcash_2').click(function() {
				setTimeout(function(){window.location = 'FastCash_2.html';}, 500);
			});
			$('.toDepositcheck_1').click(function() {
				setTimeout(function(){window.location = 'Deposit_Check_1.html';}, 500);
			});
			$('.toDepositcheck_2').click(function() {
				setTimeout(function(){window.location = 'Deposit_Check_2.html';}, 500);
			});
			$('.toDepositcheck_5').click(function() {
				setTimeout(function(){window.location = 'Deposit_Check_5.html';}, 500);
			});
			$('.toDepositcheck_6').click(function() {
				$('.back').fadeOut(300, 'linear');
				$('.buttongrey').fadeOut(300, 'linear');
				$('.box_shadow3').fadeOut(300, 'linear');
				$('.quickbalancefoot').animate({
					left: '-1000px',
				},300);
				setTimeout(function(){$('.balanceclick').removeClass('quickon')}, 200);
				setTimeout(function(){window.location = 'Deposit_Check_6.html';}, 500);
			});
			$('.toDepositcheck_8').click(function() {
				setTimeout(function(){window.location = 'Deposit_Check_8.html';}, 500);
			});
			$('.toDepositcheck_10').click(function() {
				setTimeout(function(){window.location = 'Deposit_Check_10.html';}, 500);
			});
			$('.toDepositcheck_11').click(function() {
				setTimeout(function(){window.location = 'Deposit_Check_11.html';}, 500);
			});
			$('.topin').click(function() {
				setTimeout(function(){window.location = 'PIN.html';}, 500);
			});
			
		/*End Navigations*/
			
		});

function disableselect(e){
return false
}
function reEnable(){
return true
}
document.onselectstart=new Function ("return false")
if (window.sidebar){
document.onmousedown=disableselect
document.onclick=reEnable
}





// ========================================================================================
function sayErroneousAmount(amount) {
    html_say("Error. Please Enter Only Amount in multiples of $20 or $50.", false);
}
// ========================================================================================
function sayMoneyAmount(amount) {

    var txtToSay = "";
    var cents = null;
    var dollars = null;

    var parts=amount.split(".",2);
    if(parts.length==1) {
      cents = eval(amount);
    }
    else {
      dollars = eval(parts[0]);
      cents = eval(parts[1]);
    }

    if(dollars!=null) {
        txtToSay += dollars + " dollar";
        if(dollars!=1) {
            txtToSay += "s";
        }
    }

    if(dollars==null || cents>0) {
        if(dollars!=null) {
            txtToSay += " and ";
        }
        txtToSay += cents + " cent";
        if(cents!=1) {
            txtToSay += "s";
        }
    }
    
    html_say(txtToSay, false);
    
}
// ========================================================================================
