# Common UI Components - Usage Guide

This document describes how to use the common UI components created in Task 27.

## Components Available

### 1. Button Components (`fragments/buttons.html`)

#### Action Button Set
```html
<!-- Standard action buttons for CRUD operations -->
<div th:replace="~{fragments/buttons :: action-buttons(
    itemId=${item.id},
    createUrl='/items/new',
    editUrl='/items/{id}/edit',
    deleteUrl='/items/{id}/delete',
    detailUrl='/items/{id}',
    showLabels=true
)}"></div>
```

#### Create Button
```html
<!-- Standalone create button -->
<div th:replace="~{fragments/buttons :: create-button(
    createUrl='/products/new',
    buttonText='Crear Producto',
    buttonClass='btn-success',
    icon='bi-plus-circle-fill'
)}"></div>
```

#### Form Submit Buttons
```html
<!-- Form action buttons with cancel functionality -->
<div th:replace="~{fragments/buttons :: form-buttons(
    submitText='Guardar Producto',
    cancelUrl='/products',
    cancelText='Cancelar',
    showReset=true
)}"></div>
```

### 2. Enhanced Notifications (`fragments/notification.html`)

#### Toast Notifications (Auto-dismiss)
```html
<!-- Add to layout - automatically shows toast notifications -->
<div th:replace="~{fragments/notification :: toast-notifications}"></div>

<!-- JavaScript usage -->
<script>
    // Show success notification
    showSuccess('Producto creado exitosamente', 'El producto se ha agregado al catálogo');
    
    // Show error notification
    showError('Error al guardar', 'Verifique los datos ingresados');
    
    // Show custom notification
    showToast('warning', 'Stock bajo', 'El producto tiene menos de 5 unidades', 6000);
</script>
```

### 3. Enhanced Form Fields (`fragments/form-field.html`)

#### Basic Field
```html
<!-- Enhanced form field with validation and help text -->
<div th:replace="~{fragments/form-field :: field(
    fieldType='email',
    fieldName='email',
    fieldLabel='Correo Electrónico',
    fieldValue=${user.email},
    fieldRequired=true,
    fieldIcon='bi-envelope',
    fieldHelp='Ingrese un correo electrónico válido'
)}"></div>
```

#### Select Field
```html
<!-- Select dropdown with options -->
<div th:replace="~{fragments/form-field :: field(
    fieldType='select',
    fieldName='categoryId',
    fieldLabel='Categoría',
    fieldValue=${product.categoryId},
    fieldRequired=true,
    fieldOptions=${categories},
    fieldPlaceholder='Seleccione una categoría'
)}"></div>
```

#### Checkbox Field
```html
<!-- Checkbox with enhanced styling -->
<div th:replace="~{fragments/form-field :: checkbox(
    fieldName='precioPersonalizado',
    fieldLabel='Precio personalizado',
    fieldValue=${product.precioPersonalizado},
    fieldHelp='Marque si desea establecer un precio personalizado'
)}"></div>
```

### 4. Enhanced Tables (`fragments/table.html`)

#### Responsive Table
```html
<!-- Enhanced responsive table with options -->
<div th:replace="~{fragments/table :: responsive-table(
    tableClass='table table-striped table-hover',
    tableId='productsTable',
    stickyHeader=true,
    small=true
)}">
    <thead>
        <tr>
            <th>ID</th>
            <th>Nombre</th>
            <th>Acciones</th>
        </tr>
    </thead>
    <tbody>
        <tr th:each="product : ${products}">
            <td th:text="${product.id}">1</td>
            <td th:text="${product.nombre}">Producto</td>
            <td>
                <div th:replace="~{fragments/buttons :: action-buttons(itemId=${product.id})}"></div>
            </td>
        </tr>
    </tbody>
</div>
```

### 5. Enhanced Modals (`fragments/modal.html`)

#### Responsive Modal
```html
<!-- Enhanced modal with better accessibility -->
<div th:replace="~{fragments/modal :: modal(
    modalId='productModal',
    modalTitle='Crear Producto',
    modalSize='lg',
    modalCentered=true,
    modalScrollable=true
)}">
    <div th:replace="~{this :: content}">
        <!-- Modal content here -->
    </div>
</div>
```

#### Confirmation Modal
```html
<!-- Confirmation modal with custom styling -->
<div th:replace="~{fragments/modal :: confirmation-modal(
    modalId='deleteConfirmModal',
    title='Confirmar Eliminación',
    message='¿Está seguro de que desea eliminar este producto?',
    confirmText='Eliminar',
    confirmClass='btn-danger',
    icon='bi-trash',
    iconColor='text-danger'
)}"></div>
```

### 6. Enhanced Loading States (`fragments/loading.html`)

#### Loading Spinner
```html
<!-- Customizable loading spinner -->
<div th:replace="~{fragments/loading :: spinner(
    size='lg',
    color='primary',
    text='Cargando productos...',
    showText=true
)}"></div>
```

#### Skeleton Loading
```html
<!-- Skeleton loading for cards -->
<div th:replace="~{fragments/loading :: skeleton-card(count=3)}"></div>

<!-- Skeleton loading for table rows -->
<tbody>
    <div th:replace="~{fragments/loading :: skeleton-table(rows=5, cols=4)}"></div>
</tbody>
```

#### Progress Loading
```html
<!-- Progress bar with animation -->
<div th:replace="~{fragments/loading :: progress(
    progress=65,
    text='Procesando datos...',
    showPercentage=true,
    animated=true
)}"></div>
```

## JavaScript Functions Available

### Button Functions
- `confirmDelete(id, deleteUrl, itemName)` - Shows confirmation dialog before deletion
- `updateBulkSelection()` - Updates bulk selection state
- `clearSelection()` - Clears all selected items
- `setButtonLoading(button, loading, text)` - Sets button loading state

### Notification Functions
- `showSuccess(message, details, duration)` - Shows success toast
- `showError(message, details, duration)` - Shows error toast
- `showWarning(message, details, duration)` - Shows warning toast
- `showInfo(message, details, duration)` - Shows info toast
- `showToast(type, message, details, duration)` - Shows custom toast

### Loading Functions
- `showLoadingOverlay(text)` - Shows global loading overlay
- `hideLoadingOverlay()` - Hides global loading overlay
- `showLoadingInContainer(containerId, text)` - Shows loading in specific container
- `updateProgress(progressBarId, percentage, text)` - Updates progress bar

### Modal Functions
- `showConfirmation(title, message, onConfirm, confirmText, confirmClass)` - Shows confirmation modal
- `showInfo(title, message, iconClass)` - Shows info modal
- `showImagePreview(imageSrc, altText)` - Shows image preview modal

## CSS Classes and Styling

### Responsive Breakpoints
- Mobile first design (320px minimum)
- Tablet optimized (768px)
- Desktop optimized (1024px+)

### Accessibility Features
- ARIA labels and roles
- Keyboard navigation support
- Screen reader compatibility
- Color contrast compliance (WCAG AA)
- Touch-friendly buttons (44px minimum)

### Auto-dismiss Timing
- Success messages: 3 seconds
- Info messages: 4 seconds
- Warning messages: 4 seconds
- Error messages: 5 seconds

## Requirements Addressed

- **Requirement 13.1-13.14**: Thymeleaf reusable components with proper fragment syntax
- **Requirement 17.1-17.10**: Responsive design and accessibility requirements
- **Requirement 15.10-15.12**: Auto-dismiss notifications with color differentiation
- **Requirement 14.1-14.9**: Enhanced form validation with specific error messages

All components are designed to be:
1. Responsive across all device sizes
2. Accessible (WCAG AA compliant)
3. Consistent with Bootstrap 5 styling
4. Easy to customize and extend
5. Auto-dismissing where appropriate
6. Touch-friendly for mobile devices