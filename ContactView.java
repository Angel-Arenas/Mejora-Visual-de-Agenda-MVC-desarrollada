package vallegrande.edu.pe.view;

import vallegrande.edu.pe.controller.ContactController;
import vallegrande.edu.pe.model.Contact;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 Mejora visual del contactView
 */
public class ContactView extends JFrame {
    private final ContactController controller;
    private DefaultTableModel tableModel;
    private JTable table;

    public ContactView(ContactController controller) {
        super("Agenda MVC Swing - Vallegrande");
        this.controller = controller;
        initUI();
        showWelcomeMessage();
        loadContacts();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Fuente de la letra que se cambio
        Font baseFont = new Font("Segoe UI", Font.PLAIN, 16);

        // Colores del tema oscuro
        Color bgColor = new Color(34, 40, 49);
        Color panelColor = new Color(19, 19, 27);
        Color textColor = Color.WHITE;

        // Panel principal
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        contentPanel.setBackground(bgColor);
        setContentPane(contentPanel);

        // Tabla
        tableModel = new DefaultTableModel(new String[]{"ID", "Nombre", "Email", "Teléfono"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(baseFont);
        table.setRowHeight(30);
        table.getTableHeader().setFont(baseFont.deriveFont(Font.BOLD, 18f));
        table.setForeground(textColor);
        table.setBackground(panelColor);
        table.getTableHeader().setBackground(new Color(57, 62, 70));
        table.getTableHeader().setForeground(textColor);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(panelColor);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(85, 85, 85), 1));
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel botones
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonsPanel.setBackground(bgColor);

        // Se le agrego icono al  boton agregar
        JButton addBtn = new JButton("Agregar", UIManager.getIcon("FileChooser.newFolderIcon"));
        styleButton(addBtn, new Color(13, 165, 131));

        // Se agrego al  icono al boton eliminar
        JButton deleteBtn = new JButton("Eliminar", UIManager.getIcon("OptionPane.errorIcon"));
        styleButton(deleteBtn, new Color(220, 53, 69));

        buttonsPanel.add(addBtn);
        buttonsPanel.add(deleteBtn);
        contentPanel.add(buttonsPanel, BorderLayout.SOUTH);

        // Eventos
        addBtn.addActionListener(e -> {
            showAddContactDialog();
            showToast("Contacto agregado con éxito", new Color(3, 175, 175));
        });
        deleteBtn.addActionListener(e -> {
            if (deleteSelectedContact()) {
                showToast("Contacto eliminado correctamente", new Color(186, 10, 10));
            }
        });
    }

    private void styleButton(JButton button, Color baseColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(baseColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);

        // Sombra ligera
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(baseColor.darker(), 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        // Hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(baseColor.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(baseColor);
            }
        });
    }

    private void loadContacts() {
        tableModel.setRowCount(0);
        List<Contact> contacts = controller.list();
        for (Contact c : contacts) {
            tableModel.addRow(new Object[]{c.id(), c.name(), c.email(), c.phone()});
        }
    }

    private void showAddContactDialog() {
        AddContactDialog dialog = new AddContactDialog(this, controller);
        dialog.setVisible(true);
        if (dialog.isSucceeded()) {
            loadContacts();
        }
    }

    private boolean deleteSelectedContact() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un contacto para eliminar.", "Atención", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        String id = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "¿Seguro que desea eliminar este contacto?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            controller.delete(id);
            loadContacts();
            return true;
        }
        return false;
    }

    private void showToast(String message, Color bgColor) {
        JWindow toast = new JWindow();
        JPanel panel = new JPanel();
        panel.setBackground(bgColor);
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel label = new JLabel(message);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(label);

        toast.add(panel);
        toast.pack();
        toast.setLocationRelativeTo(this);

        new Thread(() -> {
            toast.setVisible(true);
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
            toast.setVisible(false);
            toast.dispose();
        }).start();
    }

    private void showWelcomeMessage() {
        showToast("¡Bienvenido a la Agenda MVC!", new Color(251, 214, 32));
    }
}
