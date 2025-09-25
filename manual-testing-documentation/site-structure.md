# DemoWebShop Site Structure & Navigation

## Overview
**URL**: https://demowebshop.tricentis.com/
**Platform**: nopCommerce
**Test Environment**: Demo e-commerce website

## Main Navigation Structure

### Primary Navigation Menu
1. **Books** (`/books`)
   - Direct category access
   - Product listings with filtering options

2. **Computers** (`/computers`)
   - **Desktops** (`/desktops`)
   - **Notebooks** (`/notebooks`)
   - **Accessories** (`/accessories`)

3. **Electronics** (`/electronics`)
   - **Camera & Photo** (`/camera-photo`)
   - **Cell Phones** (`/cell-phones`)

4. **Apparel & Shoes** (`/apparel-shoes`)

5. **Digital Downloads** (`/digital-downloads`)

6. **Jewelry** (`/jewelry`)

7. **Gift Cards** (`/gift-cards`)

### User Account Navigation
- **Register** (`/register`) - New user registration
- **Login** (`/login`) - User authentication
- **My Account** (`/customer/info`) - Account management
- **Orders** (`/customer/orders`) - Order history
- **Addresses** (`/customer/addresses`) - Address management
- **Shopping Cart** (`/cart`) - Cart management
- **Wishlist** (`/wishlist`) - Saved items

### Utility Navigation
- **Contact Us** (`/contactus`) - Customer support
- **Search** - Global product search
- **Newsletter** - Email subscription

## Page Layout Structure

### Header Section
- **Logo** - Homepage link
- **Search Bar** - Global product search with autocomplete
- **User Links** - Register, Login, Cart, Wishlist
- **Main Navigation** - Category menu with hover dropdowns

### Main Content Areas
- **Featured Products** - Homepage product showcase
- **Category Listings** - Product grids with filtering
- **Product Details** - Individual product pages
- **Forms** - Registration, login, checkout, contact

### Sidebar (Category Pages)
- **Categories** - Nested category navigation
- **Manufacturers** - Brand filtering
- **Price Ranges** - Price filtering
- **Popular Tags** - Tag-based navigation

### Footer Section
- **Information Links** - About, shipping, privacy policy
- **Customer Service** - Support and contact information
- **My Account** - User account shortcuts
- **Follow Us** - Social media links

## Key User Flows

### 1. User Registration & Login
```
Homepage → Register → Fill Form → Email Confirmation → Login → Account Dashboard
```

### 2. Product Discovery & Purchase
```
Homepage → Category → Product List → Product Details → Add to Cart → Checkout → Order Complete
```

### 3. Account Management
```
Login → My Account → (Orders|Addresses|Info|Wishlist) → Update/View → Save
```

### 4. Search & Browse
```
Search Query → Results → Filter/Sort → Product Selection → Product Details
```

## Dynamic Content Areas

### AJAX-Enabled Features
- **Search Autocomplete** - Real-time search suggestions
- **Cart Flyout** - Hover cart preview
- **Newsletter Subscription** - Inline form submission
- **Product Quick Add** - Add to cart without page refresh

### State-Dependent Content
- **User Authentication State** - Different header links for logged in/out users
- **Cart Status** - Item count and total updates
- **Wishlist Count** - Saved items counter
- **Category Context** - Breadcrumb navigation updates

## Responsive Design Notes
- **Mobile Menu** - Collapsible navigation for mobile devices
- **Responsive Grid** - Product listings adapt to screen size
- **Touch-Friendly** - Buttons and links sized for mobile interaction

## Technical Observations

### JavaScript Libraries
- jQuery for DOM manipulation
- AJAX for dynamic content loading
- Form validation scripts
- Mobile menu toggle functionality

### CSS Framework
- Custom nopCommerce styling
- Grid-based layout system
- Hover effects and transitions

### Performance Considerations
- Image lazy loading for product galleries
- Minified JavaScript and CSS
- AJAX for cart updates to avoid full page refreshes