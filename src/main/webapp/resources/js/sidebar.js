

function JebusSidebar() {
	
	const handle = (() => {
		
		var sidebar = $(".sidebar");
 		var sidebarToggler = $(".sidebar-toggler");
 		var menuToggler = $(".menu-toggler");
 		var mainContent = $('.main-content');
		
		return {
			
			toggleSidebar() {
				if (window.innerWidth >= 996) {
					sidebar.removeClass("collapsed");
				} else {
					sidebar.addClass("collapsed");
				}
				var width = sidebar.hasClass("collapsed") ? 85 : 270;
				mainContent.css({
					"width" : `calc(100% - ${width}px)`, 
					"margin-left" : `${width}px`
				});
			},

			// Update sidebar height and menu toggle text
			toggleMenu(isMenuActive) {
				let collapsedSidebarHeight = "56px"; // Height in mobile view (collapsed)
				sidebar.css('height', isMenuActive ? `${sidebar.scrollHeight}px` : collapsedSidebarHeight);
				//menuToggler.querySelector("span").innerText = isMenuActive ? "close" : "menu";
			},
			
			init() {
				this.toggleSidebar();
				// Ensure these heights match the CSS sidebar height values
	     		//let fullSidebarHeight = "calc(100% - 32px)"; // Height in larger screen
	     		// Toggle sidebar's collapsed state
	     		sidebarToggler.on("click", () => {					
	     		  	sidebar.toggleClass("collapsed");
					this.toggleSidebar();
	     		});	     		
	     		// Toggle menu-active class and adjust height
	     		menuToggler.on("click", () => {
					this.toggleMenu(sidebar.toggleClass("menu-active"));
	     		});	     		
	     		// (Optional code): Adjust sidebar height on window resize
				
	     		window.addEventListener("resize", () => {
					this.toggleSidebar();
					/*
					if (window.innerWidth >= 1024) {
						sidebar.css('height', fullSidebarHeight);
					} else {
						sidebar.removeClass("collapsed");
						sidebar.css('height', 'auto');
						//this.toggleMenu(sidebar.hasClass("menu-active"));
					}*/
	     		});
				
			}
		}
	})();
	
	return handle;
}
