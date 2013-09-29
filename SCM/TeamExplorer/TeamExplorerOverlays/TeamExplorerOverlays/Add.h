// Add.h : Declaration of the CAdd

#pragma once
#include "resource.h"       // main symbols
#include "TeamExplorerOverlays_i.h"

#if defined(_WIN32_WCE) && !defined(_CE_DCOM) && !defined(_CE_ALLOW_SINGLE_THREADED_OBJECTS_IN_MTA)
#error "Single-threaded COM objects are not properly supported on Windows CE platform, such as the Windows Mobile platforms that do not include full DCOM support. Define _CE_ALLOW_SINGLE_THREADED_OBJECTS_IN_MTA to force ATL to support creating single-thread COM object's and allow use of it's single-threaded COM object implementations. The threading model in your rgs file was set to 'Free' as that is the only threading model supported in non DCOM Windows CE platforms."
#endif

using namespace ATL;

// CAdd
class ATL_NO_VTABLE CAdd :
	public CComObjectRootEx<CComSingleThreadModel>,
	public CComCoClass<CAdd, &CLSID_Add>,
	public IShellIconOverlayIdentifier, 
	public IDispatchImpl<IAdd, &IID_IAdd, &LIBID_TeamExplorerOverlaysLib, /*wMajor =*/ 1, /*wMinor =*/ 0>
{
public:
	CAdd()
	{
	}

	STDMETHOD(GetOverlayInfo)(LPWSTR pwszIconFile, int cchMax,int *pIndex,DWORD* pdwFlags);
	STDMETHOD(GetPriority)(int* pPriority);
	STDMETHOD(IsMemberOf)(LPCWSTR pwszPath,DWORD dwAttrib);

DECLARE_REGISTRY_RESOURCEID(IDR_ADD)

BEGIN_COM_MAP(CAdd)
	COM_INTERFACE_ENTRY(IAdd)
	COM_INTERFACE_ENTRY(IDispatch)
	COM_INTERFACE_ENTRY(IShellIconOverlayIdentifier)
END_COM_MAP()

	DECLARE_PROTECT_FINAL_CONSTRUCT()

	HRESULT FinalConstruct()
	{
		return S_OK;
	}

	void FinalRelease()
	{
	}

public:

};

OBJECT_ENTRY_AUTO(__uuidof(Add), CAdd)
