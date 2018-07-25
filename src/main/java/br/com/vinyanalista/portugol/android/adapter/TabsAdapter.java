package br.com.vinyanalista.portugol.android.adapter;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.ViewGroup;

import br.com.vinyanalista.portugol.android.AtividadePrincipal;
import br.com.vinyanalista.portugol.android.R;
import br.com.vinyanalista.portugol.android.fragment.ConsoleFragment;
import br.com.vinyanalista.portugol.android.fragment.EditorFragment;

public class TabsAdapter extends FragmentPagerAdapter {
    public static final int TAB_EDITOR = 0;
    public static final int TAB_CONSOLE = 1;

    private final int QUANTIDADE_DE_TABS = 2;
    private final int ICONES_DAS_TABS[] = new int[]{R.drawable.ic_edit_black_48dp, R.drawable.ic_desktop_windows_black_48dp};
    private final String TITULOS_DAS_TABS[] = new String[]{"Editor", "Console"};

    private AtividadePrincipal atividadePrincipal;
    private ConsoleFragment consoleFragment;
    private EditorFragment editorFragment;

    public TabsAdapter(FragmentManager fm, AtividadePrincipal atividadePrincipal) {
        super(fm);
        this.atividadePrincipal = atividadePrincipal;
    }

    @Override
    public int getCount() {
        return QUANTIDADE_DE_TABS;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case TAB_EDITOR:
                fragment = EditorFragment.newInstance(atividadePrincipal);
                break;
            case TAB_CONSOLE:
                fragment = ConsoleFragment.newInstance(atividadePrincipal);
                break;
        }
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Drawable icone = atividadePrincipal.getResources().getDrawable(ICONES_DAS_TABS[position]);
        icone.setBounds(0, 0, 24, 24);
        SpannableString sb = new SpannableString("   " + TITULOS_DAS_TABS[position]);
        ImageSpan is = new ImageSpan(icone, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(is, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // https://stackoverflow.com/a/29269509/1657502
        Fragment fragmentInstanciado = (Fragment) super.instantiateItem(container, position);
        switch (position) {
            case TAB_EDITOR:
                editorFragment = (EditorFragment) fragmentInstanciado;
                break;
            case TAB_CONSOLE:
                consoleFragment = (ConsoleFragment) fragmentInstanciado;
                break;
        }
        return fragmentInstanciado;
    }

    public ConsoleFragment getConsoleFragment() {
        return consoleFragment;
    }

    public EditorFragment getEditorFragment() {
        return editorFragment;
    }
}
